package com.bytebreeze.quickdrop.service;

import static com.bytebreeze.quickdrop.enums.ParcelStatus.*;

import com.bytebreeze.quickdrop.dto.request.CalculateShippingCostRequestDto;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.enums.PaymentStatus;
import com.bytebreeze.quickdrop.model.*;
import com.bytebreeze.quickdrop.repository.*;
import com.bytebreeze.quickdrop.util.AuthUtil;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ParcelService {
	private static final String INVALID_SENDER = "Invalid sender";
	private static final Map<ParcelStatus, Set<ParcelStatus>> validTransitions = Map.of(
			ASSIGNED, Set.of(PICKED_UP, POSTPONED),
			PICKED_UP, Set.of(IN_TRANSIT, DELIVERED, POSTPONED),
			IN_TRANSIT, Set.of(DELIVERED, POSTPONED),
			POSTPONED, Set.of(PICKED_UP, ASSIGNED)
	);

	private final SecureRandom random = new SecureRandom();
	private final ProductCategoryRepository productCategoryRepository;
	private final UserRepository userRepository;
	private final RiderRepository riderRepository;
	private final ParcelRepository parcelRepository;
	private final PaymentRepository paymentRepository;
	private final RiderService riderService;
	private final ModelMapper modelMapper;

	public Parcel bookParcel(ParcelBookingRequestDTO parcelBookingRequestDTO) {
		Parcel parcel = mapToParcel(parcelBookingRequestDTO);
		return parcelRepository.save(parcel);
	}

	public Parcel mapToParcel(ParcelBookingRequestDTO dto) {
		Parcel parcel = new Parcel();
		ProductCategory category = productCategoryRepository
				.findById(dto.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

		Optional<User> senderOptional = userRepository.findByEmail(AuthUtil.getAuthenticatedUsername());
		User sender = senderOptional.orElseThrow(() -> new IllegalArgumentException(INVALID_SENDER));

		parcel.setCategory(category);
		parcel.setDescription(dto.getDescription());
		parcel.setWeight(dto.getWeight());
		parcel.setSize(dto.getSize());
		parcel.setSender(sender);
		parcel.setPickupDivision(dto.getPickupDivision());
		parcel.setPickupDistrict(dto.getPickupDistrict());
		parcel.setPickupUpazila(dto.getPickupUpazila());
		parcel.setPickupVillage(dto.getPickupVillage());
		parcel.setReceiverName(dto.getReceiverName());
		parcel.setReceiverPhone(dto.getReceiverPhone());
		parcel.setReceiverEmail(dto.getReceiverEmail());
		parcel.setReceiverDivision(dto.getReceiverDivision());
		parcel.setReceiverDistrict(dto.getReceiverDistrict());
		parcel.setReceiverUpazila(dto.getReceiverUpazila());
		parcel.setReceiverVillage(dto.getReceiverVillage());
		parcel.setReceiverAddress(dto.getReceiverAddress());
		parcel.setPrice(dto.getPrice());
		parcel.setDistance(dto.getDistance());
		parcel.setTrackingId(this.generateUniqueTrackingId());

		return parcel;
	}

	public String generateTransactionId() {
		final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		String timePart = Long.toString(System.currentTimeMillis(), 36);
		int remainingLength = 30 - timePart.length();
		StringBuilder randomPart = new StringBuilder(remainingLength);

		for (int i = 0; i < remainingLength; i++) {
			randomPart.append(ALPHANUMERIC.charAt(this.random.nextInt(ALPHANUMERIC.length())));
		}

		return timePart + randomPart.toString();
	}

	public void savePayment(Parcel parcel, ParcelBookingRequestDTO parcelBookingRequestDTO) {
		Payment payment = new Payment();
		payment.setAmount(parcelBookingRequestDTO.getPrice());
		payment.setTransactionId(parcelBookingRequestDTO.getTransactionId());
		payment.setPaymentMethod(parcelBookingRequestDTO.getPaymentMethod());
		payment.setCurrency("BDT");
		payment.setParcel(parcel);
		payment.setUser(parcel.getSender());
		payment.setPaymentStatus(PaymentStatus.PENDING);
		paymentRepository.save(payment);
	}

	public List<Parcel> getBookedButNotDeliveredParcels() {
		User sender = userRepository
				.findByEmail(AuthUtil.getAuthenticatedUsername())
				.orElseThrow(() -> new IllegalArgumentException(INVALID_SENDER));
		return parcelRepository.findBySenderAndStatus(sender.getId(), ParcelStatus.BOOKED);
	}

	public String generateUniqueTrackingId() {
		String trackingId;
		do {
			trackingId = String.format("%06d", this.random.nextInt(900000) + 100000);
		} while (parcelRepository.existsByTrackingId(trackingId));
		return trackingId;
	}

	public List<Parcel> getParcelList() {
		User sender = userRepository
				.findByEmail(AuthUtil.getAuthenticatedUsername())
				.orElseThrow(() -> new IllegalArgumentException(INVALID_SENDER));
		return parcelRepository.getAllBySender(sender.getId());
	}

	public Parcel getParcelById(UUID id) {
		return parcelRepository.findById(id).orElseThrow(() -> new RuntimeException("Parcel not found"));
	}

	@Transactional
	public void updateParcelStatus(UUID id, ParcelStatus status) {
		Parcel parcel = getParcelById(id);

		if (!isValidStatusTransition(parcel.getStatus(), status)) {
			throw new IllegalStateException("Invalid status transition from " + parcel.getStatus() + " to " + status);
		}

		applyStatusUpdates(parcel, status);
		parcelRepository.save(parcel);
	}

	private void applyStatusUpdates(Parcel parcel, ParcelStatus status) {
		parcel.setStatus(status);
		parcel.setUpdatedAt(LocalDateTime.now());

		if (status == DELIVERED) {
			handleDeliveryCompletion(parcel);
		} else if (status == PICKED_UP) {
			parcel.setPickupTime(LocalDateTime.now());
		}
	}

	private boolean isValidStatusTransition(ParcelStatus current, ParcelStatus next) {
		return !current.equals(next) && validTransitions.getOrDefault(current, Set.of()).contains(next);
	}

	private void handleDeliveryCompletion(Parcel parcel) {
		Rider rider = parcel.getRider();
		if (rider == null) {
			throw new IllegalStateException("No rider assigned to parcel with ID: " + parcel.getId());
		}

		if (!(rider instanceof Rider)) {
			throw new IllegalStateException("Assigned rider is not a Rider instance for parcel ID: " + parcel.getId());
		}

		LocalDateTime deliveryTime = LocalDateTime.now();
		parcel.setDeliveryTime(deliveryTime);
		parcel.setDeliveredAt(deliveryTime);

		double currentBalance = rider.getRiderBalance();
		double parcelEarnings = parcel.getPrice().doubleValue();
		rider.setRiderBalance(currentBalance + parcelEarnings);
		rider.setIsAssigned(false);
		riderRepository.save(rider);
	}


	public double calculateShippingCost(CalculateShippingCostRequestDto calculateShippingCostRequestDto) {
		// definining factor - we will make it dynamic in future
		double sizeFactor = 0.2;
		double weightFactor = 0.4;
		double deliveryCharge = 100;

		double cost = sizeFactor * calculateShippingCostRequestDto.getSize();
		cost += weightFactor * calculateShippingCostRequestDto.getWeight();
		cost += deliveryCharge;

		return cost;
	}

	public List<Parcel> getRelatedParcelListOfCurrentRider() {

		Rider rider = riderService.getAuthenticatedRider();
		return parcelRepository.findByRider(rider);
	}
}

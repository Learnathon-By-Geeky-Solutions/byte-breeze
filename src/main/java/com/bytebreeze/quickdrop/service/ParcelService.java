package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.request.CalculateShippingCostRequestDto;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.enums.PaymentStatus;
import com.bytebreeze.quickdrop.model.*;
import com.bytebreeze.quickdrop.repository.*;
import com.bytebreeze.quickdrop.util.AuthUtil;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static com.bytebreeze.quickdrop.enums.ParcelStatus.*;

@Service
public class ParcelService {
	private final ProductCategoryRepository productCategoryRepository;
	private final UserRepository userRepository;
	private final RiderRepository riderRepository;
	private final ParcelRepository parcelRepository;
	private final PaymentRepository paymentRepository;

	public ParcelService(
            ProductCategoryRepository productCategoryRepository,
            UserRepository userRepository, RiderRepository riderRepository,
            ParcelRepository parcelRepository,
            PaymentRepository paymentRepository) {
		this.productCategoryRepository = productCategoryRepository;
		this.userRepository = userRepository;
        this.riderRepository = riderRepository;
        this.parcelRepository = parcelRepository;
		this.paymentRepository = paymentRepository;
	}

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
		User sender = senderOptional.orElseThrow(() -> new IllegalArgumentException("Invalid sender"));

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
		SecureRandom RANDOM = new SecureRandom();
		final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		String timePart = Long.toString(System.currentTimeMillis(), 36);
		int remainingLength = 30 - timePart.length();
		StringBuilder randomPart = new StringBuilder(remainingLength);

		for (int i = 0; i < remainingLength; i++) {
			randomPart.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
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
				.orElseThrow(() -> new IllegalArgumentException("Invalid sender"));
		return parcelRepository.findBySenderAndStatus(sender.getId(), ParcelStatus.BOOKED);
	}

	public String generateUniqueTrackingId() {
		String trackingId;
		Random random = new Random();
		do {
			trackingId = String.format("%06d", random.nextInt(900000) + 100000);
		} while (parcelRepository.existsByTrackingId(trackingId));
		return trackingId;
	}

	public List<Parcel> getParcelList() {
		User sender = userRepository
				.findByEmail(AuthUtil.getAuthenticatedUsername())
				.orElseThrow(() -> new IllegalArgumentException("Invalid sender"));
		return parcelRepository.getAllBySender(sender.getId());
	}

	public Parcel getParcelById(UUID id) {
		return parcelRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Parcel not found"));
	}

	@Transactional
	public void updateParcelStatus(UUID id, ParcelStatus status) {
		Parcel parcel = getParcelById(id);
		ParcelStatus currentStatus = parcel.getStatus();

		// Validate status transition
		if (!isValidStatusTransition(currentStatus, status)) {
			throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + status);
		}

		// Update common fields
		parcel.setStatus(status);
		parcel.setUpdatedAt(LocalDateTime.now());

		// Check if status is changing to DELIVERED
		if (status == ParcelStatus.DELIVERED) {
			handleDeliveryCompletion(parcel);
		} else {
			if (status == ParcelStatus.PICKED_UP) {
				parcel.setPickupTime(LocalDateTime.now()); // Record pickup time if applicable
			}
		}
		parcelRepository.save(parcel);
	}


	private boolean isValidStatusTransition(ParcelStatus current, ParcelStatus next) {
		// Define your valid status transitions
		if (current == next) return false;

		switch (current) {
			case ASSIGNED:
				return next == PICKED_UP || next == POSTPONED;
			case PICKED_UP:
				return next == IN_TRANSIT || next == DELIVERED || next == POSTPONED;
			case IN_TRANSIT:
				return next == DELIVERED || next == POSTPONED;
			case POSTPONED:
				return next == PICKED_UP || next == ASSIGNED;
			default:
				return false;
		}
	}

	private void handleDeliveryCompletion(Parcel parcel) {

		// Record delivery time
		LocalDateTime deliveryTime = LocalDateTime.now();
		parcel.setDeliveryTime(deliveryTime);
		parcel.setDeliveredAt(deliveryTime);

		// Add earnings to rider's balance
		Rider rider = parcel.getRider();
		if (rider instanceof Rider) {
			double currentBalance = rider.getRiderBalance();
			double parcelEarnings = parcel.getPrice().doubleValue(); // Convert BigDecimal to double
			rider.setRiderBalance(currentBalance + parcelEarnings);
			rider.setIsAssigned(false);
			riderRepository.save(rider); // Save updated rider
		} else if (rider == null) {
			throw new IllegalStateException("No rider assigned to parcel with ID: " + parcel.getId());
		} else {
			throw new IllegalStateException("Assigned rider is not a Rider instance for parcel ID: " + parcel.getId());
		}

		// Industry-standard best practices
		System.out.println("Parcel " + parcel.getTrackingId() + " delivered at " + deliveryTime +
				". Earnings $" + parcel.getPrice() + " added to rider " + rider.getId());
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

}

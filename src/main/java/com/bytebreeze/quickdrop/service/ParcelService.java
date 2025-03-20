package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.enums.PaymentStatus;
import com.bytebreeze.quickdrop.model.Parcel;
import com.bytebreeze.quickdrop.model.Payment;
import com.bytebreeze.quickdrop.model.ProductCategory;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class ParcelService {
	private final ProductCategoryRepository productCategoryRepository;
	private final UserRepository userRepository;
	private final ParcelRepository parcelRepository;
	private final PaymentRepository paymentRepository;

	public ParcelService(
			ProductCategoryRepository productCategoryRepository,
			UserRepository userRepository,
			ParcelRepository parcelRepository,
			PaymentRepository paymentRepository) {
		this.productCategoryRepository = productCategoryRepository;
		this.userRepository = userRepository;
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
		parcel.setReceiverName(dto.getReceiverName());
		parcel.setReceiverPhone(dto.getReceiverPhone());
		parcel.setReceiverEmail(dto.getReceiverEmail());
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
}

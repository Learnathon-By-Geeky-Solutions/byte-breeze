package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.model.Parcel;
import com.bytebreeze.quickdrop.model.ProductCategory;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;
@Service
public class ParcelService {
    private final ProductCategoryRepository productCategoryRepository;
    private final UserRepository userRepository;
    private final ParcelRepository parcelRepository;

    public ParcelService(ProductCategoryRepository productCategoryRepository, UserRepository userRepository, ParcelRepository parcelRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.userRepository = userRepository;
        this.parcelRepository = parcelRepository;
    }

    public void bookParcel(ParcelBookingRequestDTO parcelBookingRequestDTO) {
        Parcel parcel = mapToParcel(parcelBookingRequestDTO);
        parcelRepository.save(parcel);
    }

    public Parcel mapToParcel(ParcelBookingRequestDTO dto) {
        Parcel parcel = new Parcel();
        ProductCategory category = productCategoryRepository.findById(dto.getCategoryId())
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
}

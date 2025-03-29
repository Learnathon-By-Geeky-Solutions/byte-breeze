package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.RiderDashboardResponseDTO;
import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.dto.response.RiderApprovalByAdminResponseDTO;
import com.bytebreeze.quickdrop.dto.response.RiderViewCurrentParcelsResponseDTO;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.custom.ParcelAlreadyAssignedException;
import com.bytebreeze.quickdrop.exception.custom.ParcelNotFoundException;
import com.bytebreeze.quickdrop.exception.custom.UserNotFoundException;
import com.bytebreeze.quickdrop.mapper.RegisterRiderMapper;
import com.bytebreeze.quickdrop.model.Parcel;
import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.repository.RiderRepository;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RiderService {

	private final RiderRepository riderRepository;
	private final ParcelRepository parcelRepository;
	private final UserRepository userRepository;

	// private final OnboardRiderMapper onboardRiderMapper;
	private final FileStorageService fileStorageService;

	private final PasswordEncoder passwordEncoder;
	private final RegisterRiderMapper registerRiderMapper;

	public boolean isEmailAlreadyInUse(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	public Rider findByRiderId(UUID id) {
		return riderRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	public Rider getAuthenticatedRider() {
		String authenticatedRiderEmail = AuthUtil.getAuthenticatedUsername();
		Optional<Rider> rider = riderRepository.findByEmail(authenticatedRiderEmail);
		return rider.orElseThrow(() -> new UserNotFoundException("User not Authenticated"));
	}

	public RiderDashboardResponseDTO riderDashboardResponse() {

		Rider rider = getAuthenticatedRider();
		RiderDashboardResponseDTO responseDTO = new RiderDashboardResponseDTO();

		responseDTO.setFullName(rider.getFullName());
		responseDTO.setVerificationStatus(rider.getVerificationStatus());
		responseDTO.setIsAvailable(rider.getIsAvailable());
		responseDTO.setRiderAvgRating(rider.getRiderAvgRating());
		responseDTO.setRiderBalance(rider.getRiderBalance());

		return responseDTO;
	}

	public Rider registerRider(RiderRegistrationRequestDTO dto) {

		if (isEmailAlreadyInUse(dto.getEmail())) {

			throw new AlreadyExistsException("Provided email already registered");
		}

		Rider rider = new Rider();

		dto.setPassword(passwordEncoder.encode(dto.getPassword()));

		rider = registerRiderMapper.toEntity(dto);
		if (rider.getRoles() == null || rider.getRoles().isEmpty()) {
			rider.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_RIDER)));
		}
		return riderRepository.save(rider);
	}

	public Rider onboardRider(UUID riderId, RiderOnboardingDTO riderOnboardingDTO) {

		Rider rider = findByRiderId(riderId);

		// rider = onboardRiderMapper.toEntity(riderOnboardingDTO);

		// Checking that given NID No. is previously taken or not.?
		if (riderRepository
				.findByNationalIdNumber(riderOnboardingDTO.getNationalIdNumber())
				.isPresent()) {
			throw new AlreadyExistsException("Provided National ID Number already registered");
		}

		rider.setDateOfBirth(riderOnboardingDTO.getDateOfBirth());
		rider.setGender(riderOnboardingDTO.getGender());
		rider.setAddress(riderOnboardingDTO.getAddress());
		rider.setUpazila(riderOnboardingDTO.getUpazila());
		rider.setDistrict(riderOnboardingDTO.getDistrict());
		rider.setPostalCode(riderOnboardingDTO.getPostalCode());
		rider.setVehicleType(riderOnboardingDTO.getVehicleType());
		rider.setVehicleModel(riderOnboardingDTO.getVehicleModel());
		rider.setVehicleRegistrationNumber(riderOnboardingDTO.getVehicleRegistrationNumber());
		rider.setVehicleInsuranceProvider(riderOnboardingDTO.getVehicleInsuranceProvider());
		rider.setInsuranceExpiryDate(riderOnboardingDTO.getInsuranceExpiryDate());
		rider.setLicenseExpiryDate(riderOnboardingDTO.getLicenseExpiryDate());
		rider.setNationalIdNumber(riderOnboardingDTO.getNationalIdNumber());
		rider.setDriversLicenseNumber(riderOnboardingDTO.getDriversLicenseNumber());

		// Store files & get paths
		rider.setNationalIdFrontPath(fileStorageService.storeFile(riderOnboardingDTO.getNationalIdFront()));
		rider.setNationalIdBackPath(fileStorageService.storeFile(riderOnboardingDTO.getNationalIdBack()));
		rider.setDriversLicenseFrontPath(fileStorageService.storeFile(riderOnboardingDTO.getDriversLicenseFront()));
		rider.setDriversLicenseBackPath(fileStorageService.storeFile(riderOnboardingDTO.getDriversLicenseBack()));
		rider.setVehicleRegistrationDocumentPath(
				fileStorageService.storeFile(riderOnboardingDTO.getVehicleRegistrationDocument()));
		rider.setInsuranceDocumentPath(fileStorageService.storeFile(riderOnboardingDTO.getInsuranceDocument()));

		// Set verification status
		rider.setVerificationStatus(VerificationStatus.PENDING);

		return riderRepository.save(rider);
	}

	public List<RiderApprovalByAdminResponseDTO> getPendingRiders() {

		List<Rider> pendingRiders = riderRepository.findByVerificationStatus(VerificationStatus.PENDING);

		return pendingRiders.stream()
				.map(rider -> new RiderApprovalByAdminResponseDTO(
						rider.getId(), rider.getFullName(), rider.getEmail(), rider.getPhoneNumber()))
				.collect(Collectors.toList());
	}

	public void updateRiderVerificationStatus(UUID riderId, VerificationStatus verificationStatus) {

		Rider rider = findByRiderId(riderId);
		rider.setVerificationStatus(verificationStatus);
		riderRepository.save(rider);
	}

	public void updateRiderStatus(Boolean status) {

		Rider rider = getAuthenticatedRider();

		rider.setIsAvailable(status);

		riderRepository.save(rider);
	}

	public List<RiderViewCurrentParcelsResponseDTO> CurrentParcelsForRider() {

		List<Parcel> currentAvailableParcels = parcelRepository.findByStatusAndRiderIsNull(ParcelStatus.BOOKED);

		// Logging level
		if (currentAvailableParcels.isEmpty()) {
			log.warn("No available parcels found with status: BOOKED");
			// Handle empty case (e.g., return early or throw exception)
		} else {
			log.info("Found {} available parcels with status: BOOKED", currentAvailableParcels.size());
		}

		return currentAvailableParcels.stream()
				.map(parcel -> {
					RiderViewCurrentParcelsResponseDTO dto = new RiderViewCurrentParcelsResponseDTO();
					dto.setId(parcel.getId());
					dto.setTrackingId(parcel.getTrackingId());
					// dto.setCategory(parcel.getCategory().getCategory().toString()); // Assuming ProductCategory has
					// getName()
					dto.setPickupDistrict(parcel.getPickupDistrict());
					dto.setPickupUpazila(parcel.getPickupUpazila());
					dto.setPickupVillage(parcel.getPickupVillage());
					dto.setReceiverDistrict(parcel.getReceiverDistrict());
					dto.setReceiverUpazila(parcel.getReceiverUpazila());
					dto.setReceiverVillage(parcel.getReceiverVillage());
					dto.setPrice(parcel.getPrice());

					return dto;
				})
				.collect(Collectors.toList());
	}

	@Transactional
	public void acceptParcelDelivery(UUID parcelId){

		Rider rider = getAuthenticatedRider();
		Parcel parcel = parcelRepository.findById(parcelId)
				.orElseThrow(() -> new ParcelNotFoundException("Parcel not found with ID: " + parcelId));

		if (parcel.getRider() != null) {
			throw new AlreadyExistsException("Parcel already assigned to another rider");
		}

		if(rider.getIsAssigned()){
			throw new ParcelAlreadyAssignedException("You have already assigned to a parcel");
		}
		rider.setIsAssigned(true);

		parcel.setRider(rider);
		parcel.setStatus(ParcelStatus.ASSIGNED);
		parcel.setAssignedAt(LocalDateTime.now());
		parcelRepository.save(parcel);

	}


	public List<Parcel> getAssignedParcelByRider(Rider rider) {

		List<Parcel> parcels = parcelRepository.findByStatusAndRider(ParcelStatus.ASSIGNED, rider);

		log.info("Found {} available assigned parcels with status: Assigned and rider email {}", parcels.size(), rider.getEmail());
		return parcels;
	}

}

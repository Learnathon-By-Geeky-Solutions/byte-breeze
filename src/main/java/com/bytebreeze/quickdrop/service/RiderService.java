package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.request.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.request.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.dto.response.RiderApprovalByAdminResponseDTO;
import com.bytebreeze.quickdrop.dto.response.RiderDashboardResponseDTO;
import com.bytebreeze.quickdrop.dto.response.RiderDetailsResponseDto;
import com.bytebreeze.quickdrop.dto.response.RiderViewCurrentParcelsResponseDTO;
import com.bytebreeze.quickdrop.entity.ParcelEntity;
import com.bytebreeze.quickdrop.entity.RiderEntity;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
import com.bytebreeze.quickdrop.exception.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.ParcelAlreadyAssignedException;
import com.bytebreeze.quickdrop.exception.ParcelNotFoundException;
import com.bytebreeze.quickdrop.exception.UserNotFoundException;
import com.bytebreeze.quickdrop.mapper.RegisterRiderMapper;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.repository.RiderRepository;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
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

	private final FileStorageService fileStorageService;

	private final PasswordEncoder passwordEncoder;
	private final RegisterRiderMapper registerRiderMapper;

	public boolean isEmailAlreadyInUse(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	public RiderEntity findByRiderId(UUID id) {
		return riderRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	public RiderEntity getAuthenticatedRider() {
		String authenticatedRiderEmail = AuthUtil.getAuthenticatedUsername();
		Optional<RiderEntity> rider = riderRepository.findByEmail(authenticatedRiderEmail);
		return rider.orElseThrow(() -> new UserNotFoundException("User not Authenticated"));
	}

	public RiderDashboardResponseDTO riderDashboardResponse() {

		RiderEntity rider = getAuthenticatedRider();
		RiderDashboardResponseDTO responseDTO = new RiderDashboardResponseDTO();

		responseDTO.setFullName(rider.getFullName());
		responseDTO.setVerificationStatus(rider.getVerificationStatus());
		responseDTO.setIsAvailable(rider.getIsAvailable());
		responseDTO.setRiderAvgRating(rider.getRiderAvgRating());
		responseDTO.setRiderBalance(rider.getRiderBalance());

		return responseDTO;
	}

	public RiderEntity registerRider(RiderRegistrationRequestDTO dto) {

		if (isEmailAlreadyInUse(dto.getEmail())) {

			throw new AlreadyExistsException("Provided email already registered");
		}

		dto.setPassword(passwordEncoder.encode(dto.getPassword()));

		RiderEntity rider = registerRiderMapper.toEntity(dto);
		if (rider.getRoles() == null || rider.getRoles().isEmpty()) {
			rider.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_RIDER)));
		}
		return riderRepository.save(rider);
	}

	public RiderEntity onboardRider(UUID riderId, RiderOnboardingDTO riderOnboardingDTO) {

		RiderEntity rider = findByRiderId(riderId);

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

		List<RiderEntity> pendingRiders = riderRepository.findByVerificationStatus(VerificationStatus.PENDING);

		return pendingRiders.stream()
				.map(rider -> new RiderApprovalByAdminResponseDTO(
						rider.getId(), rider.getFullName(), rider.getEmail(), rider.getPhoneNumber()))
				.toList();
	}

	public void updateRiderVerificationStatus(UUID riderId, VerificationStatus verificationStatus) {

		RiderEntity rider = findByRiderId(riderId);
		rider.setVerificationStatus(verificationStatus);
		riderRepository.save(rider);
	}

	public void updateRiderStatus(Boolean status) {

		RiderEntity rider = getAuthenticatedRider();

		rider.setIsAvailable(status);

		riderRepository.save(rider);
	}

	public List<RiderViewCurrentParcelsResponseDTO> currentParcelsForRider() {

		List<ParcelEntity> currentAvailableParcels = parcelRepository.findByStatusAndRiderIsNull(ParcelStatus.BOOKED);

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
				.toList();
	}

	@Transactional
	public void acceptParcelDelivery(UUID parcelId) {

		RiderEntity rider = getAuthenticatedRider();
		ParcelEntity parcel = parcelRepository
				.findById(parcelId)
				.orElseThrow(() -> new ParcelNotFoundException("Parcel not found with ID: " + parcelId));

		if (parcel.getRider() != null) {
			throw new AlreadyExistsException("Parcel already assigned to another rider");
		}

		if (rider.getIsAssigned()) {
			throw new ParcelAlreadyAssignedException("You have already assigned to a parcel");
		}
		rider.setIsAssigned(true);

		parcel.setRider(rider);
		parcel.setStatus(ParcelStatus.ASSIGNED);
		parcel.setAssignedAt(LocalDateTime.now());
		parcelRepository.save(parcel);
	}

	public List<ParcelEntity> getAssignedParcelByRider(RiderEntity rider) {

		List<ParcelStatus> desiredStatuses =
				Arrays.asList(ParcelStatus.ASSIGNED, ParcelStatus.PICKED_UP, ParcelStatus.IN_TRANSIT);

		List<ParcelEntity> parcels = parcelRepository.findByStatusInAndRider(desiredStatuses, rider);

		log.info(
				"Found {} available assigned parcels with status: Assigned and rider email {}",
				parcels.size(),
				rider.getEmail());
		return parcels;
	}

	public RiderDetailsResponseDto getRiderDetails(UUID riderId) {
		RiderEntity rider =
				riderRepository.findById(riderId).orElseThrow(() -> new UserNotFoundException("Rider not found"));
		return mapToDto(rider);
	}

	public RiderDetailsResponseDto mapToDto(RiderEntity rider) {
		RiderDetailsResponseDto dto = new RiderDetailsResponseDto();
		dto.setId(rider.getId());
		dto.setFullName(rider.getFullName());
		dto.setEmail(rider.getEmail());
		dto.setPhoneNumber(rider.getPhoneNumber());
		dto.setProfilePicture(rider.getProfilePicture());
		dto.setDateOfBirth(rider.getDateOfBirth());
		dto.setGender(rider.getGender());
		dto.setAddress(rider.getAddress());
		dto.setUpazila(rider.getUpazila());
		dto.setDistrict(rider.getDistrict());
		dto.setPostalCode(rider.getPostalCode());
		dto.setVehicleType(rider.getVehicleType());
		dto.setVehicleModel(rider.getVehicleModel());
		dto.setVehicleRegistrationNumber(rider.getVehicleRegistrationNumber());
		dto.setVehicleInsuranceProvider(rider.getVehicleInsuranceProvider());
		dto.setInsuranceExpiryDate(rider.getInsuranceExpiryDate());
		dto.setVehicleRegistrationDocumentPath(rider.getVehicleRegistrationDocumentPath());
		dto.setInsuranceDocumentPath(rider.getInsuranceDocumentPath());
		dto.setNationalIdNumber(rider.getNationalIdNumber());
		dto.setNationalIdFrontPath(rider.getNationalIdFrontPath());
		dto.setNationalIdBackPath(rider.getNationalIdBackPath());
		dto.setDriversLicenseNumber(rider.getDriversLicenseNumber());
		dto.setLicenseExpiryDate(rider.getLicenseExpiryDate());
		dto.setDriversLicenseFrontPath(rider.getDriversLicenseFrontPath());
		dto.setDriversLicenseBackPath(rider.getDriversLicenseBackPath());
		dto.setVerificationStatus(rider.getVerificationStatus());
		dto.setIsAvailable(rider.getIsAvailable());
		dto.setIsAssigned(rider.getIsAssigned());
		dto.setRiderAvgRating(rider.getRiderAvgRating());
		dto.setTotalRating(rider.getTotalRating());
		dto.setRiderBalance(rider.getRiderBalance());
		return dto;
	}
}

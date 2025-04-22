package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.request.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.request.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.dto.response.*;
import com.bytebreeze.quickdrop.entity.*;
import com.bytebreeze.quickdrop.enums.*;
import com.bytebreeze.quickdrop.exception.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.ParcelAlreadyAssignedException;
import com.bytebreeze.quickdrop.exception.UserNotFoundException;
import com.bytebreeze.quickdrop.mapper.RegisterRiderMapper;
import com.bytebreeze.quickdrop.repository.*;
import com.bytebreeze.quickdrop.util.AuthUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RiderServiceTest {

	@InjectMocks
	private RiderService riderService;

	@Mock
	private RiderRepository riderRepository;

	@Mock
	private ParcelRepository parcelRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FileStorageService fileStorageService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RegisterRiderMapper registerRiderMapper;

	private RiderEntity rider;

	@BeforeEach
	void setUp() {
		rider = new RiderEntity();
		rider.setId(UUID.randomUUID());
		rider.setEmail("rider@example.com");
		rider.setIsAvailable(true);
		rider.setFullName("John Doe");
		rider.setVerificationStatus(VerificationStatus.PENDING);
	}

	@Test
	void isEmailAlreadyInUse_true() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new RiderEntity()));
		assertTrue(riderService.isEmailAlreadyInUse("test@example.com"));
	}

	@Test
	void isEmailAlreadyInUse_false() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
		assertFalse(riderService.isEmailAlreadyInUse("test@example.com"));
	}

	@Test
	void findByRiderId_found() {
		when(riderRepository.findById(rider.getId())).thenReturn(Optional.of(rider));
		assertEquals(rider, riderService.findByRiderId(rider.getId()));
	}

	@Test
	void findByRiderId_notFound() {
		UUID id = UUID.randomUUID();
		when(riderRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> riderService.findByRiderId(id));
	}

	@Test
	void getAuthenticatedRider_success() {
		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("rider@example.com");
			when(riderRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(rider));
			RiderEntity result = riderService.getAuthenticatedRider();
			assertEquals(rider, result);
		}
	}

	@Test
	void getAuthenticatedRider_fail() {
		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("unknown@example.com");
			when(riderRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
			assertThrows(UserNotFoundException.class, () -> riderService.getAuthenticatedRider());
		}
	}

	@Test
	void riderDashboardResponse_success() {
		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("rider@example.com");
			when(riderRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(rider));
			RiderDashboardResponseDTO dto = riderService.riderDashboardResponse();
			assertEquals("John Doe", dto.getFullName());
			assertEquals(VerificationStatus.PENDING, dto.getVerificationStatus());
		}
	}

	@Test
	void registerRider_success() {
		RiderRegistrationRequestDTO dto = new RiderRegistrationRequestDTO();
		dto.setEmail("rider@example.com");
		dto.setPassword("1234");
		RiderEntity mapped = new RiderEntity();

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(passwordEncoder.encode("1234")).thenReturn("encoded");
		when(registerRiderMapper.toEntity(dto)).thenReturn(mapped);
		when(riderRepository.save(mapped)).thenReturn(mapped);

		RiderEntity result = riderService.registerRider(dto);
		assertNotNull(result);
	}

	@Test
	void registerRider_emailExists() {
		RiderRegistrationRequestDTO dto = new RiderRegistrationRequestDTO();
		dto.setEmail("test@example.com");
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new RiderEntity()));
		assertThrows(AlreadyExistsException.class, () -> riderService.registerRider(dto));
	}

	@Test
	void getPendingRiders_success() {
		rider.setPhoneNumber("123");
		when(riderRepository.findByVerificationStatus(VerificationStatus.PENDING))
				.thenReturn(List.of(rider));
		List<RiderApprovalByAdminResponseDTO> result = riderService.getPendingRiders();
		assertEquals(1, result.size());
	}

	@Test
	void updateRiderVerificationStatus_success() {
		when(riderRepository.findById(rider.getId())).thenReturn(Optional.of(rider));
		riderService.updateRiderVerificationStatus(rider.getId(), VerificationStatus.VERIFIED);
		verify(riderRepository).save(any());
	}

	@Test
	void updateRiderStatus_success() {
		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("rider@example.com");
			when(riderRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(rider));
			riderService.updateRiderStatus(false);
			verify(riderRepository).save(any());
		}
	}

	@Test
	void currentParcelsForRider_success() {
		ParcelEntity parcel = new ParcelEntity();
		parcel.setId(UUID.randomUUID());
		parcel.setStatus(ParcelStatus.BOOKED);
		parcel.setTrackingId("T123");
		when(parcelRepository.findByStatusAndRiderIsNull(ParcelStatus.BOOKED)).thenReturn(List.of(parcel));
		assertEquals(1, riderService.currentParcelsForRider().size());
	}

	@Test
	void acceptParcelDelivery_success() {
		ParcelEntity parcel = new ParcelEntity();
		parcel.setId(UUID.randomUUID());
		parcel.setStatus(ParcelStatus.BOOKED);

		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("rider@example.com");
			rider.setIsAssigned(false);
			when(riderRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(rider));
			when(parcelRepository.findById(parcel.getId())).thenReturn(Optional.of(parcel));

			riderService.acceptParcelDelivery(parcel.getId());
			verify(parcelRepository).save(parcel);
		}
	}

	@Test
	void acceptParcelDelivery_alreadyAssignedToOther() {
		// Arrange
		ParcelEntity parcel = new ParcelEntity();
		parcel.setId(UUID.randomUUID());
		parcel.setRider(new RiderEntity()); // Simulate parcel already assigned

		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("rider@example.com");
			when(riderRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(rider));
			when(parcelRepository.findById(parcel.getId())).thenReturn(Optional.of(parcel));

			UUID parcelId = parcel.getId();

			assertThrows(
					AlreadyExistsException.class,
					() -> riderService.acceptParcelDelivery(parcelId),
					"Should throw AlreadyExistsException when parcel is assigned to another rider");
		}
	}

	@Test
	void acceptParcelDelivery_riderAlreadyAssigned() {
		// Arrange
		ParcelEntity parcel = new ParcelEntity();
		parcel.setId(UUID.randomUUID());

		try (MockedStatic<AuthUtil> mockedStatic = mockStatic(AuthUtil.class)) {
			mockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("rider@example.com");
			rider.setIsAssigned(true); // Rider is already assigned to another parcel
			when(riderRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(rider));
			when(parcelRepository.findById(parcel.getId())).thenReturn(Optional.of(parcel));

			UUID parcelId = parcel.getId();
			assertThrows(
					ParcelAlreadyAssignedException.class,
					() -> riderService.acceptParcelDelivery(parcelId),
					"Should throw ParcelAlreadyAssignedException when rider is already assigned");
		}
	}

	@Test
	void getAssignedParcelByRider_success() {
		when(parcelRepository.findByStatusInAndRider(anyList(), eq(rider))).thenReturn(List.of(new ParcelEntity()));
		List<ParcelEntity> result = riderService.getAssignedParcelByRider(rider);
		assertEquals(1, result.size());
	}

	@Test
	void getRiderDetails_success() {
		when(riderRepository.findById(rider.getId())).thenReturn(Optional.of(rider));
		assertNotNull(riderService.getRiderDetails(rider.getId()));
	}

	@Test
	void getRiderDetails_notFound() {
		UUID id = UUID.randomUUID();
		when(riderRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> riderService.getRiderDetails(id));
	}

	@Test
	void mapToDto_success() {
		RiderDetailsResponseDto dto = riderService.mapToDto(rider);
		assertEquals(rider.getEmail(), dto.getEmail());
	}

	@Test
	void onboardRider_success() {
		// Arrange
		UUID riderId = UUID.randomUUID(); // Rider ID to be used
		RiderOnboardingDTO riderOnboardingDTO = new RiderOnboardingDTO();

		// Setup dates
		LocalDate dateOfBirth = LocalDate.parse("1990-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
		LocalDate insuranceExpirayDate = LocalDate.parse("2040-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
		LocalDate licenseExpireyDate = LocalDate.parse("2040-01-01", DateTimeFormatter.ISO_LOCAL_DATE);

		// Set necessary fields for the DTO
		riderOnboardingDTO.setNationalIdNumber("123456789");
		riderOnboardingDTO.setDateOfBirth(dateOfBirth);
		riderOnboardingDTO.setGender(Gender.MALE);
		riderOnboardingDTO.setAddress("Some Address");
		riderOnboardingDTO.setUpazila("Some Upazila");
		riderOnboardingDTO.setDistrict("Some District");
		riderOnboardingDTO.setPostalCode("1234");
		riderOnboardingDTO.setVehicleType(VehicleType.BYCYCLE);
		riderOnboardingDTO.setVehicleModel("Honda");
		riderOnboardingDTO.setVehicleRegistrationNumber("899021234CD");
		riderOnboardingDTO.setVehicleInsuranceProvider("InsuranceCo");
		riderOnboardingDTO.setInsuranceExpiryDate(insuranceExpirayDate);
		riderOnboardingDTO.setLicenseExpiryDate(licenseExpireyDate);
		riderOnboardingDTO.setDriversLicenseNumber("892412345");

		// Mock repository behavior for finding rider and checking for existing National ID
		RiderEntity existingRider = new RiderEntity();
		existingRider.setId(riderId); // Ensure the riderId is set
		existingRider.setNationalIdNumber("123456789");
		existingRider.setDateOfBirth(dateOfBirth);
		existingRider.setGender(Gender.MALE);
		existingRider.setAddress("Some Address");
		existingRider.setUpazila("Some Upazila");
		existingRider.setDistrict("Some District");
		existingRider.setPostalCode("1234");
		existingRider.setVehicleType(VehicleType.BYCYCLE);
		existingRider.setVehicleModel("Honda");
		existingRider.setVehicleRegistrationNumber("899021234CD");
		existingRider.setVehicleInsuranceProvider("InsuranceCo");
		existingRider.setInsuranceExpiryDate(insuranceExpirayDate);
		existingRider.setLicenseExpiryDate(licenseExpireyDate);
		existingRider.setDriversLicenseNumber("892412345");

		// Mock behavior for findByRiderId to return the existing rider
		when(riderRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingRider));
		when(riderRepository.findByNationalIdNumber(anyString()))
				.thenReturn(Optional.empty()); // Ensure no existing rider with same National ID
		when(fileStorageService.storeFile(any())).thenReturn("file/path");

		// Mock save to return the rider
		when(riderRepository.save(any(RiderEntity.class))).thenReturn(existingRider);

		// Act
		RiderEntity onboardedRider = riderService.onboardRider(riderId, riderOnboardingDTO);

		// Assert
		assertNotNull(onboardedRider);
		assertEquals(existingRider.getNationalIdNumber(), onboardedRider.getNationalIdNumber());
		assertEquals(existingRider.getDateOfBirth(), onboardedRider.getDateOfBirth());
		assertEquals(existingRider.getGender(), onboardedRider.getGender());
		assertEquals(existingRider.getVehicleModel(), onboardedRider.getVehicleModel());
		verify(riderRepository).save(onboardedRider); // Verify that the save method is called
	}
}

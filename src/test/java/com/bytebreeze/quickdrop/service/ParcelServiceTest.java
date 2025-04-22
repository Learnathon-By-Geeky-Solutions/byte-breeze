package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.request.CalculateShippingCostRequestDto;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.entity.*;
import com.bytebreeze.quickdrop.entity.RiderEntity;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import com.bytebreeze.quickdrop.repository.RiderRepository;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {

	@Mock
	private ProductCategoryRepository productCategoryRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RiderRepository riderRepository;

	@Mock
	private ParcelRepository parcelRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private RiderService riderService;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private ParcelService parcelService;

	private UserEntity sender;
	private ProductCategoryEntity category;
	private RiderEntity rider;
	private MockedStatic<AuthUtil> authUtilMockedStatic;

	@BeforeEach
	void setUp() {
		sender = new UserEntity();
		sender.setId(UUID.randomUUID());
		sender.setEmail("sender@example.com");

		category = new ProductCategoryEntity();
		category.setId(UUID.randomUUID());
		category.setCategory("Electronics");

		rider = new RiderEntity();
		rider.setId(UUID.randomUUID());
		rider.setRiderBalance(0.0);
		rider.setIsAssigned(true);

		authUtilMockedStatic = mockStatic(AuthUtil.class);
	}

	@AfterEach
	void tearDown() {
		authUtilMockedStatic.close();
	}

	@Test
	void testBookParcel_Success() {
		ParcelBookingRequestDTO dto = createParcelBookingRequestDTO();
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");
		when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
		when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));
		when(parcelRepository.save(any(ParcelEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ParcelEntity result = parcelService.bookParcel(dto);

		assertNotNull(result);
		assertEquals(sender, result.getSender());
		assertEquals(category, result.getCategory());
		verify(parcelRepository, times(1)).save(any(ParcelEntity.class));
	}

	@Test
	void testBookParcel_InvalidSender() {
		ParcelBookingRequestDTO dto = createParcelBookingRequestDTO();
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");
		when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));
		when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> parcelService.bookParcel(dto));
	}

	@Test
	void testBookParcel_InvalidCategory() {
		ParcelBookingRequestDTO dto = createParcelBookingRequestDTO();
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");
		when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> parcelService.bookParcel(dto));
	}

	@Test
	void testGenerateTransactionId() {
		String transactionId = parcelService.generateTransactionId();
		assertNotNull(transactionId);
		assertEquals(30, transactionId.length());
	}

	@Test
	void testSavePayment_Success() {
		ParcelEntity parcel = new ParcelEntity();
		parcel.setSender(sender);
		ParcelBookingRequestDTO dto = createParcelBookingRequestDTO();
		when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		parcelService.savePayment(parcel, dto);

		verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
	}

	@Test
	void testGetBookedButNotDeliveredParcels_Success() {
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");
		when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
		List<ParcelEntity> parcels = Collections.singletonList(new ParcelEntity());
		when(parcelRepository.findBySenderAndStatus(sender.getId(), ParcelStatus.BOOKED))
				.thenReturn(parcels);

		List<ParcelEntity> result = parcelService.getBookedButNotDeliveredParcels();

		assertEquals(parcels, result);
		verify(parcelRepository, times(1)).findBySenderAndStatus(sender.getId(), ParcelStatus.BOOKED);
	}

	@Test
	void testGetBookedButNotDeliveredParcels_InvalidSender() {
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");
		when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> parcelService.getBookedButNotDeliveredParcels());
	}

	@Test
	void testGenerateUniqueTrackingId() {
		when(parcelRepository.existsByTrackingId(anyString())).thenReturn(false);

		String trackingId = parcelService.generateUniqueTrackingId();

		assertNotNull(trackingId);
		assertEquals(6, trackingId.length());
		verify(parcelRepository, atLeastOnce()).existsByTrackingId(anyString());
	}

	@Test
	void testGetParcelList_Success() {
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");
		when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
		List<ParcelEntity> parcels = Collections.singletonList(new ParcelEntity());
		when(parcelRepository.getAllBySender(sender.getId())).thenReturn(parcels);

		List<ParcelEntity> result = parcelService.getParcelList();

		assertEquals(parcels, result);
		verify(parcelRepository, times(1)).getAllBySender(sender.getId());
	}

	@Test
	void testGetParcelById_Success() {
		UUID parcelId = UUID.randomUUID();
		ParcelEntity parcel = new ParcelEntity();
		when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

		ParcelEntity result = parcelService.getParcelById(parcelId);

		assertEquals(parcel, result);
		verify(parcelRepository, times(1)).findById(parcelId);
	}

	@Test
	void testGetParcelById_NotFound() {
		UUID parcelId = UUID.randomUUID();
		when(parcelRepository.findById(parcelId)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> parcelService.getParcelById(parcelId));
	}

	@Test
	void testUpdateParcelStatus_Delivered_Success() {
		UUID parcelId = UUID.randomUUID();
		ParcelEntity parcel = new ParcelEntity();
		parcel.setStatus(ParcelStatus.IN_TRANSIT);
		parcel.setRider(rider);
		parcel.setPrice(BigDecimal.valueOf(100.0));
		when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
		when(parcelRepository.save(any(ParcelEntity.class))).thenReturn(parcel);
		when(riderRepository.save(any(RiderEntity.class))).thenReturn(rider);

		parcelService.updateParcelStatus(parcelId, ParcelStatus.DELIVERED);

		assertEquals(ParcelStatus.DELIVERED, parcel.getStatus());
		assertNotNull(parcel.getDeliveredAt());
		assertEquals(100.0, rider.getRiderBalance());
		assertFalse(rider.getIsAssigned());
		verify(parcelRepository, times(1)).save(parcel);
		verify(riderRepository, times(1)).save(rider);
	}

	@Test
	void testUpdateParcelStatus_PickedUp_Success() {
		UUID parcelId = UUID.randomUUID();
		ParcelEntity parcel = new ParcelEntity();
		parcel.setStatus(ParcelStatus.ASSIGNED);
		when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));
		when(parcelRepository.save(any(ParcelEntity.class))).thenReturn(parcel);

		parcelService.updateParcelStatus(parcelId, ParcelStatus.PICKED_UP);

		assertEquals(ParcelStatus.PICKED_UP, parcel.getStatus());
		assertNotNull(parcel.getPickupTime());
		verify(parcelRepository, times(1)).save(parcel);
	}

	@Test
	void testUpdateParcelStatus_InvalidTransition() {
		UUID parcelId = UUID.randomUUID();
		ParcelEntity parcel = new ParcelEntity();
		parcel.setStatus(ParcelStatus.BOOKED);
		when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

		assertThrows(
				IllegalStateException.class, () -> parcelService.updateParcelStatus(parcelId, ParcelStatus.DELIVERED));
	}

	@Test
	void testUpdateParcelStatus_Delivered_NoRider() {
		UUID parcelId = UUID.randomUUID();
		ParcelEntity parcel = new ParcelEntity();
		parcel.setStatus(ParcelStatus.IN_TRANSIT);
		parcel.setRider(null);
		when(parcelRepository.findById(parcelId)).thenReturn(Optional.of(parcel));

		assertThrows(
				IllegalStateException.class, () -> parcelService.updateParcelStatus(parcelId, ParcelStatus.DELIVERED));
	}

	@Test
	void testCalculateShippingCost() {
		CalculateShippingCostRequestDto dto = new CalculateShippingCostRequestDto(10.0, 5.0);
		double expectedCost = (0.2 * 10.0) + (0.4 * 5.0) + 100.0; // 2 + 2 + 100 = 104

		double result = parcelService.calculateShippingCost(dto);

		assertEquals(expectedCost, result, 0.001);
	}

	@Test
	void testGetRelatedParcelListOfCurrentRider_Success() {
		List<ParcelEntity> parcels = Collections.singletonList(new ParcelEntity());
		when(riderService.getAuthenticatedRider()).thenReturn(rider);
		when(parcelRepository.findByRider(rider)).thenReturn(parcels);

		List<ParcelEntity> result = parcelService.getRelatedParcelListOfCurrentRider();

		assertEquals(parcels, result);
		verify(riderService, times(1)).getAuthenticatedRider();
		verify(parcelRepository, times(1)).findByRider(rider);
	}

	private ParcelBookingRequestDTO createParcelBookingRequestDTO() {
		ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
		dto.setCategoryId(category.getId());
		dto.setDescription("Test Parcel");
		dto.setWeight(5.0);
		dto.setSize(10.0);
		dto.setPickupDivision("Division");
		dto.setPickupDistrict("District");
		dto.setPickupUpazila("Upazila");
		dto.setPickupVillage("Village");
		dto.setReceiverName("Receiver");
		dto.setReceiverPhone("+8801741543475");
		dto.setReceiverEmail("receiver@example.com");
		dto.setReceiverDivision("Receiver Division");
		dto.setReceiverDistrict("Receiver District");
		dto.setReceiverUpazila("Receiver Upazila");
		dto.setReceiverVillage("Receiver Village");
		dto.setReceiverAddress("Receiver Address");
		dto.setPrice(BigDecimal.valueOf(100.0));
		dto.setDistance(50.0);
		dto.setPaymentMethod("Cash");
		dto.setTransactionId("TX123");
		return dto;
	}
}

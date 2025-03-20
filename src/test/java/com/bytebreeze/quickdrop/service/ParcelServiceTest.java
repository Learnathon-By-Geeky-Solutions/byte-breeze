package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParcelServiceTest {

	@Mock
	private ProductCategoryRepository productCategoryRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ParcelRepository parcelRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private ParcelService parcelService;

	@Test
	void testMapToParcel_Success() {
		ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
		UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000001");
		dto.setCategoryId(categoryId);
		dto.setDescription("Test Parcel");
		dto.setWeight(5.0);
		dto.setSize(2.0);
		dto.setReceiverName("Receiver Name");
		dto.setReceiverPhone("1234567890");
		dto.setReceiverEmail("receiver@example.com");
		dto.setReceiverAddress("123 Street, City");
		dto.setPrice(BigDecimal.valueOf(100.0));
		dto.setDistance(10.0);
		dto.setPaymentMethod("CreditCard");
		dto.setTransactionId(null);

		ProductCategory category = new ProductCategory();
		category.setId(categoryId);

		User sender = new User();
		sender.setEmail("sender@example.com");

		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));
			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));

			Parcel parcel = parcelService.mapToParcel(dto);

			assertNotNull(parcel);
			assertEquals(category, parcel.getCategory());
			assertEquals("Test Parcel", parcel.getDescription());
			assertEquals(5.0, parcel.getWeight());
			assertEquals(2.0, parcel.getSize());
			assertEquals(sender, parcel.getSender());
			assertEquals("Receiver Name", parcel.getReceiverName());
			assertEquals("1234567890", parcel.getReceiverPhone());
			assertEquals("receiver@example.com", parcel.getReceiverEmail());
			assertEquals("123 Street, City", parcel.getReceiverAddress());
			assertEquals(BigDecimal.valueOf(100.0), parcel.getPrice());
			assertEquals(10.0, parcel.getDistance());
			assertNotNull(parcel.getTrackingId());
			assertTrue(parcel.getTrackingId().matches("\\d{6}"));
		}
	}

	@Test
	void testMapToParcel_InvalidCategory() {
		ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
		UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000002");
		dto.setCategoryId(categoryId);

		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.empty());

			IllegalArgumentException exception =
					assertThrows(IllegalArgumentException.class, () -> parcelService.mapToParcel(dto));
			assertEquals("Invalid category ID", exception.getMessage());
		}
	}

	@Test
	void testMapToParcel_InvalidSender() {
		ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
		UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
		dto.setCategoryId(categoryId);

		ProductCategory category = new ProductCategory();
		category.setId(categoryId);

		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));
			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.empty());

			IllegalArgumentException exception =
					assertThrows(IllegalArgumentException.class, () -> parcelService.mapToParcel(dto));
			assertEquals("Invalid sender", exception.getMessage());
		}
	}

	@Test
	void testBookParcel() {
		ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
		UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000004");
		dto.setCategoryId(categoryId);
		dto.setDescription("Test Parcel");
		dto.setWeight(5.0);
		dto.setSize(2.0);
		dto.setReceiverName("Receiver Name");
		dto.setReceiverPhone("1234567890");
		dto.setReceiverEmail("receiver@example.com");
		dto.setReceiverAddress("123 Street, City");
		dto.setPrice(BigDecimal.valueOf(100.0));
		dto.setDistance(10.0);
		dto.setPaymentMethod("CreditCard");
		dto.setTransactionId("TX123");

		ProductCategory category = new ProductCategory();
		category.setId(categoryId);

		User sender = new User();
		sender.setEmail("sender@example.com");

		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.of(category));
			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
			when(parcelRepository.save(any(Parcel.class))).thenAnswer(invocation -> invocation.getArgument(0));

			Parcel savedParcel = parcelService.bookParcel(dto);

			assertNotNull(savedParcel);
			assertEquals("Test Parcel", savedParcel.getDescription());
			assertEquals(category, savedParcel.getCategory());
			assertEquals(sender, savedParcel.getSender());
			assertEquals(BigDecimal.valueOf(100.0), savedParcel.getPrice());
			verify(parcelRepository, times(1)).save(any(Parcel.class));
		}
	}

	@Test
	void testGenerateTransactionId() {
		String transactionId = parcelService.generateTransactionId();

		assertNotNull(transactionId);
		assertEquals(30, transactionId.length());
		assertTrue(transactionId.matches("[0-9A-Za-z]{30}"));
	}

	@Test
	void testSavePayment() {
		Parcel parcel = new Parcel();
		User sender = new User();
		sender.setEmail("sender@example.com");
		parcel.setSender(sender);

		ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
		dto.setPrice(BigDecimal.valueOf(150.0));
		dto.setTransactionId("TRANSACTION123");
		dto.setPaymentMethod("CreditCard");

		parcelService.savePayment(parcel, dto);

		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository, times(1)).save(paymentCaptor.capture());
		Payment capturedPayment = paymentCaptor.getValue();

		assertEquals(BigDecimal.valueOf(150.0), capturedPayment.getAmount());
		assertEquals("TRANSACTION123", capturedPayment.getTransactionId());
		assertEquals("CreditCard", capturedPayment.getPaymentMethod());
		assertEquals("BDT", capturedPayment.getCurrency());
		assertEquals(parcel, capturedPayment.getParcel());
		assertEquals(sender, capturedPayment.getUser());
		assertEquals(PaymentStatus.PENDING, capturedPayment.getPaymentStatus());
	}

	@Test
	void testGetBookedButNotDeliveredParcels_Success() {
		User sender = new User();
		sender.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		sender.setEmail("sender@example.com");

		Parcel parcel1 = new Parcel();
		parcel1.setSender(sender);
		parcel1.setStatus(ParcelStatus.BOOKED);

		List<Parcel> bookedParcels = Arrays.asList(parcel1);

		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
			when(parcelRepository.findBySenderAndStatus(sender.getId(), ParcelStatus.BOOKED))
					.thenReturn(bookedParcels);

			List<Parcel> result = parcelService.getBookedButNotDeliveredParcels();

			assertNotNull(result);
			assertEquals(1, result.size());
			assertEquals(parcel1, result.get(0));
			verify(parcelRepository, times(1)).findBySenderAndStatus(sender.getId(), ParcelStatus.BOOKED);
		}
	}

	@Test
	void testGetBookedButNotDeliveredParcels_InvalidSender() {
		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.empty());

			IllegalArgumentException exception =
					assertThrows(IllegalArgumentException.class, () -> parcelService.getBookedButNotDeliveredParcels());
			assertEquals("Invalid sender", exception.getMessage());
			verify(parcelRepository, never()).findBySenderAndStatus(any(UUID.class), any(ParcelStatus.class));
		}
	}

	@Test
	void testGenerateUniqueTrackingId_UniqueOnFirstTry() {
		when(parcelRepository.existsByTrackingId(anyString())).thenReturn(false);

		String trackingId = parcelService.generateUniqueTrackingId();

		assertNotNull(trackingId);
		assertEquals(6, trackingId.length());
		assertTrue(trackingId.matches("\\d{6}"));
		assertTrue(Integer.parseInt(trackingId) >= 100000 && Integer.parseInt(trackingId) <= 999999);
		verify(parcelRepository, times(1)).existsByTrackingId(trackingId);
	}

	@Test
	void testGenerateUniqueTrackingId_CollisionThenUnique() {
		when(parcelRepository.existsByTrackingId(anyString())).thenReturn(true).thenReturn(false);

		String trackingId = parcelService.generateUniqueTrackingId();

		assertNotNull(trackingId);
		assertEquals(6, trackingId.length());
		assertTrue(trackingId.matches("\\d{6}"));
		assertTrue(Integer.parseInt(trackingId) >= 100000 && Integer.parseInt(trackingId) <= 999999);
		verify(parcelRepository, times(2)).existsByTrackingId(anyString());
	}

	@Test
	void testGetParcelList_Success() {
		User sender = new User();
		sender.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		sender.setEmail("sender@example.com");

		Parcel parcel1 = new Parcel();
		parcel1.setSender(sender);

		List<Parcel> parcelList = Arrays.asList(parcel1);

		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
			when(parcelRepository.getAllBySender(sender.getId())).thenReturn(parcelList);

			List<Parcel> result = parcelService.getParcelList();

			assertNotNull(result);
			assertEquals(1, result.size());
			assertEquals(parcel1, result.get(0));
			verify(parcelRepository, times(1)).getAllBySender(sender.getId());
		}
	}

	@Test
	void testGetParcelList_InvalidSender() {
		try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
			authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

			when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.empty());

			IllegalArgumentException exception =
					assertThrows(IllegalArgumentException.class, () -> parcelService.getParcelList());
			assertEquals("Invalid sender", exception.getMessage());
			verify(parcelRepository, never()).getAllBySender(any(UUID.class));
		}
	}
}

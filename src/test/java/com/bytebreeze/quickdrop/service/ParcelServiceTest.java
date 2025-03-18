package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
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

  @Mock private ProductCategoryRepository productCategoryRepository;

  @Mock private UserRepository userRepository;

  @Mock private ParcelRepository parcelRepository;

  @Mock private PaymentRepository paymentRepository;

  @InjectMocks private ParcelService parcelService;

  @Test
  void testMapToParcel_Success() {
    // Arrange
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

    // Mock static method AuthUtil.getAuthenticatedUsername()
    try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
      authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

      when(productCategoryRepository.findById(dto.getCategoryId()))
          .thenReturn(Optional.of(category));
      when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));

      // Act
      Parcel parcel = parcelService.mapToParcel(dto);

      // Assert
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
    }
  }

  @Test
  void testMapToParcel_InvalidCategory() {
    // Arrange
    ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
    UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    dto.setCategoryId(categoryId);

    try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
      authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

      when(productCategoryRepository.findById(dto.getCategoryId())).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> parcelService.mapToParcel(dto));
      assertEquals("Invalid category ID", exception.getMessage());
    }
  }

  @Test
  void testMapToParcel_InvalidSender() {
    // Arrange
    ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
    UUID categoryId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    dto.setCategoryId(categoryId);

    ProductCategory category = new ProductCategory();
    category.setId(categoryId);

    try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
      authUtilMock.when(AuthUtil::getAuthenticatedUsername).thenReturn("sender@example.com");

      when(productCategoryRepository.findById(dto.getCategoryId()))
          .thenReturn(Optional.of(category));
      when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> parcelService.mapToParcel(dto));
      assertEquals("Invalid sender", exception.getMessage());
    }
  }

  @Test
  void testBookParcel() {
    // Arrange
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

      when(productCategoryRepository.findById(dto.getCategoryId()))
          .thenReturn(Optional.of(category));
      when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
      // Simulate saving by returning the same parcel passed to save
      when(parcelRepository.save(any(Parcel.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      // Act
      Parcel savedParcel = parcelService.bookParcel(dto);

      // Assert
      assertNotNull(savedParcel);
      assertEquals("Test Parcel", savedParcel.getDescription());
      assertEquals(category, savedParcel.getCategory());
      assertEquals(sender, savedParcel.getSender());
      assertEquals(BigDecimal.valueOf(100.0), savedParcel.getPrice());
      // Additional assertions can be added as needed
    }
  }

  @Test
  void testGenerateTransactionId() {
    // Act
    String transactionId = parcelService.generateTransactionId();

    // Assert
    assertNotNull(transactionId);
    assertEquals(30, transactionId.length());
    // Verify that the transaction ID is alphanumeric
    assertTrue(transactionId.matches("[0-9A-Za-z]{30}"));
  }

  @Test
  void testSavePayment() {
    // Arrange
    Parcel parcel = new Parcel();
    User sender = new User();
    sender.setEmail("sender@example.com");
    parcel.setSender(sender);

    ParcelBookingRequestDTO dto = new ParcelBookingRequestDTO();
    dto.setPrice(BigDecimal.valueOf(150.0));
    dto.setTransactionId("TRANSACTION123");
    dto.setPaymentMethod("CreditCard");

    // Act
    parcelService.savePayment(parcel, dto);

    // Assert
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
}

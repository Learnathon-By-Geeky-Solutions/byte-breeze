package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzPaymentInitResponseDto;
import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzValidatorResponse;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.entity.UserEntity;
import com.bytebreeze.quickdrop.util.SSLCommerzUtil;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SSLCommerzPaymentServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private HashVerificationService hashVerificationService;

	private SSLCommerzPaymentService paymentService;

	private ParcelBookingRequestDTO parcelBookingRequestDTO;
	private UserEntity sender;
	private Map<String, String> requestParameters;

	private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	@BeforeEach
	void setUp() throws Exception {
		paymentService = new SSLCommerzPaymentService(restTemplate, hashVerificationService);
		setPrivateField(paymentService, "storeId", "testStoreId");
		setPrivateField(paymentService, "storePasswd", "testStorePasswd");
		setPrivateField(paymentService, "paymentInitializationUrl", "http://test.url/init");
		setPrivateField(paymentService, "baseUrl", "http://base.url");
		setPrivateField(paymentService, "sslczURL", "http://sslcommerz.url");

		parcelBookingRequestDTO = new ParcelBookingRequestDTO();
		parcelBookingRequestDTO.setPrice(new BigDecimal("100.00"));
		parcelBookingRequestDTO.setTransactionId("tran123");
		parcelBookingRequestDTO.setPaymentMethod("visa");
		parcelBookingRequestDTO.setCategoryId(UUID.randomUUID());

		sender = new UserEntity();
		sender.setFullName("John Doe");
		sender.setEmail("john@example.com");

		requestParameters = new HashMap<>();
		requestParameters.put("val_id", "testValId");
	}

	// Tests for getPaymentUrl

	@Test
	void testGetPaymentUrl_Success() {
		SSLCommerzPaymentInitResponseDto responseDto = new SSLCommerzPaymentInitResponseDto();
		responseDto.setStatus("SUCCESS");
		SSLCommerzPaymentInitResponseDto.Desc desc = new SSLCommerzPaymentInitResponseDto.Desc();
		desc.setGw("visa");
		desc.setRedirectGatewayURL("http://redirect.url");
		responseDto.setDesc(Collections.singletonList(desc));

		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity =
				new ResponseEntity<>(responseDto, HttpStatus.OK);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		String result = paymentService.getPaymentUrl(parcelBookingRequestDTO, sender);
		assertEquals("http://redirect.url", result);
	}

	@Test
	void testGetPaymentUrl_Failure() {
		SSLCommerzPaymentInitResponseDto responseDto = new SSLCommerzPaymentInitResponseDto();
		responseDto.setStatus("FAILED");
		responseDto.setFailedreason("Invalid data");

		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity =
				new ResponseEntity<>(responseDto, HttpStatus.OK);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		Exception exception =
				assertThrows(Exception.class, () -> paymentService.getPaymentUrl(parcelBookingRequestDTO, sender));
		assertTrue(exception.getMessage().contains("Failed: Invalid data"));
	}

	@Test
	void testGetPaymentUrl_NoMatchingGateway_ThrowsException() {
		SSLCommerzPaymentInitResponseDto responseDto = new SSLCommerzPaymentInitResponseDto();
		responseDto.setStatus("SUCCESS");
		SSLCommerzPaymentInitResponseDto.Desc desc = new SSLCommerzPaymentInitResponseDto.Desc();
		desc.setGw("mastercard");
		desc.setRedirectGatewayURL("http://redirect.url");
		responseDto.setDesc(Collections.singletonList(desc));

		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity =
				new ResponseEntity<>(responseDto, HttpStatus.OK);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		parcelBookingRequestDTO.setPaymentMethod("visa");
		RuntimeException exception = assertThrows(
				RuntimeException.class, () -> paymentService.getPaymentUrl(parcelBookingRequestDTO, sender));
		assertEquals("Payment method not found", exception.getMessage());
	}

	@Test
	void testGetPaymentUrl_NonOkStatusCode_ThrowsException() {
		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity =
				new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		Exception exception =
				assertThrows(Exception.class, () -> paymentService.getPaymentUrl(parcelBookingRequestDTO, sender));
		assertTrue(exception.getMessage().contains("Failed to send payment request"));
	}

	@Test
	void testGetPaymentUrl_NullResponseBody_ThrowsException() {
		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		Exception exception =
				assertThrows(Exception.class, () -> paymentService.getPaymentUrl(parcelBookingRequestDTO, sender));
		assertTrue(exception.getMessage().contains("Failed: No response body"));
	}

	@Test
	void testOrderValidate_HashVerificationFails() throws Exception {
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("val_id", "val123");

		when(hashVerificationService.verifyIPNHash(anyMap(), eq("testStorePasswd")))
				.thenReturn(false);

		boolean result = paymentService.orderValidate("tran123", "100.00", "BDT", requestParameters);
		assertFalse(result);
	}

	@Test
	void buildValidationUrl_ValidParameters_ReturnsCorrectlyEncodedUrl() {
		// Act
		String result = invokeBuildValidationUrl(requestParameters);

		// Assert
		String expectedUrl = "http://sslcommerz.url/validator/api/validationserverAPI.php?val_id=testValId"
				+ "&store_id=testStoreId&store_passwd=testStorePasswd&v=1&format=json";
		assertEquals(expectedUrl, result);
	}

	@Test
	void getValidatedResponse_EmptyJsonResponse_ReturnsNull() throws IOException {
		// Arrange
		try (MockedStatic<SSLCommerzUtil> mockedUtil = Mockito.mockStatic(SSLCommerzUtil.class)) {
			mockedUtil
					.when(() -> SSLCommerzUtil.getByOpeningJavaUrlConnection(anyString()))
					.thenReturn("");

			// Act
			SSLCommerzValidatorResponse result = invokeGetValidatedResponse(requestParameters);

			// Assert
			assertNull(result);
			mockedUtil.verify(() -> SSLCommerzUtil.extractValidatorResponse(anyString()), Mockito.never());
		}
	}

	// Helper methods to invoke private methods using reflection
	private String invokeBuildValidationUrl(Map<String, String> params) {
		try {
			java.lang.reflect.Method method =
					SSLCommerzPaymentService.class.getDeclaredMethod("buildValidationUrl", Map.class);
			method.setAccessible(true);
			return (String) method.invoke(paymentService, params);
		} catch (Exception e) {
			throw new RuntimeException("Failed to invoke buildValidationUrl", e);
		}
	}

	private SSLCommerzValidatorResponse invokeGetValidatedResponse(Map<String, String> params) throws IOException {
		try {
			java.lang.reflect.Method method =
					SSLCommerzPaymentService.class.getDeclaredMethod("getValidatedResponse", Map.class);
			method.setAccessible(true);
			return (SSLCommerzValidatorResponse) method.invoke(paymentService, params);
		} catch (Exception e) {
			throw new RuntimeException("Failed to invoke getValidatedResponse", e);
		}
	}
}

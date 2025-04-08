package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzPaymentInitResponseDto;
import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzValidatorResponse;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.util.SSLCommerzUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class SSLCommerzPaymentServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private SSLCommerzPaymentService paymentService;

	private ParcelBookingRequestDTO parcelBookingRequestDTO;
	private User sender;

	@BeforeEach
	void setUp() {
		paymentService = new SSLCommerzPaymentService(restTemplate, paymentRepository);
		paymentService.storeId = "testStoreId";
		paymentService.storePasswd = "testStorePasswd";
		paymentService.paymentInitializationUrl = "http://test.url/init";
		paymentService.baseUrl = "http://base.url";
		paymentService.sslczURL = "http://sslcommerz.url";

		parcelBookingRequestDTO = new ParcelBookingRequestDTO();
		parcelBookingRequestDTO.setPrice(new BigDecimal("100.00"));
		parcelBookingRequestDTO.setTransactionId("tran123");
		parcelBookingRequestDTO.setPaymentMethod("visa");
		parcelBookingRequestDTO.setCategoryId(UUID.randomUUID());

		sender = new User();
		sender.setFullName("John Doe");
		sender.setEmail("john@example.com");
	}

	@Test
	void testGetPaymentUrl_Success() {
		SSLCommerzPaymentInitResponseDto responseDto = new SSLCommerzPaymentInitResponseDto();
		responseDto.setStatus("SUCCESS");
		List<SSLCommerzPaymentInitResponseDto.Desc> descList = new ArrayList<>();
		SSLCommerzPaymentInitResponseDto.Desc desc = new SSLCommerzPaymentInitResponseDto.Desc();
		desc.setGw("visa");
		desc.setRedirectGatewayURL("http://redirect.url");
		descList.add(desc);
		responseDto.setDesc(descList);

		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity =
				new ResponseEntity<>(responseDto, HttpStatus.OK);

		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		String result = paymentService.getPaymentUrl(parcelBookingRequestDTO, sender);

		assertEquals("http://redirect.url", result);
		verify(restTemplate)
				.postForEntity(
						eq(paymentService.paymentInitializationUrl),
						any(HttpEntity.class),
						eq(SSLCommerzPaymentInitResponseDto.class));
	}

	@Test
	void testGetPaymentUrl_Failure() {
		SSLCommerzPaymentInitResponseDto responseDto = new SSLCommerzPaymentInitResponseDto();
		responseDto.setStatus("FAILED");
		responseDto.setFailedreason("Invalid data");

		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity =
				new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);

		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(SSLCommerzPaymentInitResponseDto.class)))
				.thenReturn(responseEntity);

		RuntimeException exception = assertThrows(
				RuntimeException.class, () -> paymentService.getPaymentUrl(parcelBookingRequestDTO, sender));

		assertTrue(exception.getMessage().contains("Failed to send payment request"));
		verify(restTemplate)
				.postForEntity(
						eq(paymentService.paymentInitializationUrl),
						any(HttpEntity.class),
						eq(SSLCommerzPaymentInitResponseDto.class));
	}

	@Test
	void testOrderValidate_Success() throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("verify_key", "val_id,tran_id,currency_amount,currency_type");
		requestParameters.put("val_id", "val123");
		requestParameters.put("tran_id", "tran123");
		requestParameters.put("currency_amount", "100.00");
		requestParameters.put("currency_type", "BDT");

		// Compute the expected hash using TreeMap for sorted order
		TreeMap<String, String> sortedParams = new TreeMap<>();
		String[] keys = requestParameters.get("verify_key").split(",");
		for (String key : keys) {
			sortedParams.put(key, requestParameters.get(key));
		}
		sortedParams.put("store_passwd", paymentService.md5("testStorePasswd"));
		String hashString = SSLCommerzPaymentService.getParamsString(sortedParams, false);
		String expectedHash = paymentService.md5(hashString);
		requestParameters.put("verify_sign", expectedHash);

		String jsonResponse =
				"{\"status\":\"VALID\",\"tran_id\":\"tran123\",\"currency_amount\":\"100.00\",\"currency_type\":\"BDT\"}";

		SSLCommerzValidatorResponse validatorResponse = new SSLCommerzValidatorResponse();
		validatorResponse.setStatus("VALID");
		validatorResponse.setTran_id("tran123");
		validatorResponse.setCurrency_amount("100.00");
		validatorResponse.setCurrency_type("BDT");

		try (MockedStatic<SSLCommerzUtil> mockedStatic = mockStatic(SSLCommerzUtil.class)) {
			mockedStatic
					.when(() -> SSLCommerzUtil.getByOpeningJavaUrlConnection(anyString()))
					.thenReturn(jsonResponse);
			mockedStatic
					.when(() -> SSLCommerzUtil.extractValidatorResponse(jsonResponse))
					.thenReturn(validatorResponse);

			boolean result = paymentService.orderValidate("tran123", "100.00", "BDT", requestParameters);

			assertTrue(result);
			mockedStatic.verify(() -> SSLCommerzUtil.getByOpeningJavaUrlConnection(anyString()));
			mockedStatic.verify(() -> SSLCommerzUtil.extractValidatorResponse(jsonResponse));
		}
	}

	@Test
	void testOrderValidate_HashVerificationFailure()
			throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("verify_sign", "wrongHash");
		requestParameters.put("verify_key", "val_id");
		requestParameters.put("val_id", "val123");

		boolean result = paymentService.orderValidate("tran123", "100.00", "BDT", requestParameters);

		assertFalse(result);
		assertEquals("Unable to verify hash", paymentService.error);
	}

	@Test
	void testOrderValidate_AmountMismatch() throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("verify_key", "val_id,tran_id,currency_amount,currency_type");
		requestParameters.put("val_id", "val123");
		requestParameters.put("tran_id", "tran123");
		requestParameters.put("currency_amount", "200.00");
		requestParameters.put("currency_type", "BDT");

		// Compute the expected hash using TreeMap for sorted order
		TreeMap<String, String> sortedParams = new TreeMap<>();
		String[] keys = requestParameters.get("verify_key").split(",");
		for (String key : keys) {
			sortedParams.put(key, requestParameters.get(key));
		}
		sortedParams.put("store_passwd", paymentService.md5("testStorePasswd"));
		String hashString = SSLCommerzPaymentService.getParamsString(sortedParams, false);
		String expectedHash = paymentService.md5(hashString);
		requestParameters.put("verify_sign", expectedHash);

		String jsonResponse =
				"{\"status\":\"VALID\",\"tran_id\":\"tran123\",\"currency_amount\":\"200.00\",\"currency_type\":\"BDT\"}";

		SSLCommerzValidatorResponse validatorResponse = new SSLCommerzValidatorResponse();
		validatorResponse.setStatus("VALID");
		validatorResponse.setTran_id("tran123");
		validatorResponse.setCurrency_amount("200.00");
		validatorResponse.setCurrency_type("BDT");

		try (MockedStatic<SSLCommerzUtil> mockedStatic = mockStatic(SSLCommerzUtil.class)) {
			mockedStatic
					.when(() -> SSLCommerzUtil.getByOpeningJavaUrlConnection(anyString()))
					.thenReturn(jsonResponse);
			mockedStatic
					.when(() -> SSLCommerzUtil.extractValidatorResponse(jsonResponse))
					.thenReturn(validatorResponse);

			boolean result = paymentService.orderValidate("tran123", "100.00", "BDT", requestParameters);

			assertFalse(result);
			assertEquals("Currency Amount not matching", paymentService.error);
			mockedStatic.verify(() -> SSLCommerzUtil.getByOpeningJavaUrlConnection(anyString()));
			mockedStatic.verify(() -> SSLCommerzUtil.extractValidatorResponse(jsonResponse));
		}
	}

	@Test
	void testIpnHashVerify_Success() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("verify_key", "key1,key2");
		requestParameters.put("key1", "value1");
		requestParameters.put("key2", "value2");

		// Compute the expected hash using TreeMap for sorted order
		TreeMap<String, String> sortedParams = new TreeMap<>();
		String[] keys = requestParameters.get("verify_key").split(",");
		for (String key : keys) {
			sortedParams.put(key, requestParameters.get(key));
		}
		sortedParams.put("store_passwd", paymentService.md5("testStorePasswd"));
		String hashString = SSLCommerzPaymentService.getParamsString(sortedParams, false);
		String expectedHash = paymentService.md5(hashString);
		requestParameters.put("verify_sign", expectedHash);

		Boolean result = paymentService.ipnHashVerify(requestParameters);

		assertTrue(result);
	}

	@Test
	void testIpnHashVerify_Failure_NoVerifySign() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, String> requestParameters = new HashMap<>();
		requestParameters.put("verify_sign", "");
		requestParameters.put("verify_key", "key1");
		requestParameters.put("key1", "value1");

		Boolean result = paymentService.ipnHashVerify(requestParameters);

		assertFalse(result);
	}

	@Test
	void testMd5() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String input = "testString";
		String result = paymentService.md5(input);

		assertNotNull(result);
		assertEquals(32, result.length()); // MD5 hash is 32 characters long
	}

	@Test
	void testGetParamsString() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("key1", "value1");
		params.put("key2", "value2");

		String result = SSLCommerzPaymentService.getParamsString(params, false);
		assertEquals("key1=value1&key2=value2", result);

		String encodedResult = SSLCommerzPaymentService.getParamsString(params, true);
		assertEquals("key1=value1&key2=value2", encodedResult); // Simple values don't change with encoding
	}

	@Test
	void testGetParamsString_Empty() throws Exception {
		Map<String, String> params = new HashMap<>();
		String result = SSLCommerzPaymentService.getParamsString(params, false);
		assertEquals("", result);
	}
}

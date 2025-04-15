package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzPaymentInitResponseDto;
import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzValidatorResponse;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.exception.custom.SSLCommerzPaymentInitializationException;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.util.SSLCommerzUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SSLCommerzPaymentService implements PaymentService {
	private static final String UTF_8 = "UTF-8";
	private static final String VERIFY_KEY = "verify_key";
	private static final String VERIFY_SIGN = "verify_sign";
	private RestTemplate restTemplate;

	@Value("${sslcommerz.store-id}")
	String storeId;

	@Value("${sslcommerz.store-passwd}")
	String storePasswd;

	@Value("${sslcommerz.init-url}")
	String paymentInitializationUrl;

	private String successUrl = "/sslcommerz/success";
	private String failureUrl = "/sslcommerz/failure";
	private String errorUrl = "/sslcommerz/cancel";

	@Value("${sslcommerz.base-url}")
	String baseUrl;

	String generateHash;
	String error;

	@Value("${sslcommerz.validation-url}")
	String sslczURL;

	private String validationURL = "/validator/api/validationserverAPI.php";

	public SSLCommerzPaymentService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private String extractRedirectUrl(
			SSLCommerzPaymentInitResponseDto sslCommerzPaymentInitResponseDto, String paymentMethod) {
		return sslCommerzPaymentInitResponseDto.getDesc().stream()
				.filter(desc -> desc.getGw().equalsIgnoreCase(paymentMethod))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Payment method not found"))
				.getRedirectGatewayURL();
	}

	@Override
	public String getPaymentUrl(ParcelBookingRequestDTO parcelBookingRequestDTO, User sender) {
		MultiValueMap<String, String> formData = buildFormData(parcelBookingRequestDTO, sender);
		HttpEntity<MultiValueMap<String, String>> requestEntity = buildHttpEntity(formData);

		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity = sendPaymentRequest(requestEntity);

		return handlePaymentResponse(responseEntity, parcelBookingRequestDTO.getPaymentMethod());
	}


	private MultiValueMap<String, String> buildFormData(ParcelBookingRequestDTO dto, User sender) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("store_id", this.storeId);
		formData.add("store_passwd", this.storePasswd);
		formData.add("total_amount", dto.getPrice().toString());
		formData.add("currency", "BDT");
		formData.add("tran_id", dto.getTransactionId());
		formData.add("success_url", baseUrl + successUrl);
		formData.add("fail_url", baseUrl + failureUrl);
		formData.add("cancel_url", baseUrl + errorUrl);
		formData.add("cus_name", sender.getFullName());
		formData.add("cus_email", sender.getEmail());

		// Optional fields (left blank or placeholders)
		formData.add("cus_add1", "");
		formData.add("cus_city", "");
		formData.add("cus_state", "");
		formData.add("cus_postcode", "");
		formData.add("cus_country", "");
		formData.add("cus_phone", "");
		formData.add("ship_name", sender.getFullName());
		formData.add("ship_add1", "");
		formData.add("ship_city", "");
		formData.add("ship_state", "");
		formData.add("ship_postcode", "");
		formData.add("ship_country", "");
		formData.add("multi_card_name", "");
		formData.add("value_a", "");
		formData.add("value_b", "");
		formData.add("value_c", "");
		formData.add("value_d", "");
		formData.add("product_name", dto.getCategoryId().toString());
		formData.add("product_category", dto.getCategoryId().toString());
		formData.add("product_profile", "general");

		return formData;
	}

	private HttpEntity<MultiValueMap<String, String>> buildHttpEntity(MultiValueMap<String, String> formData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HttpEntity<>(formData, headers);
	}

	private ResponseEntity<SSLCommerzPaymentInitResponseDto> sendPaymentRequest(HttpEntity<MultiValueMap<String, String>> requestEntity) {
		return restTemplate.postForEntity(
				paymentInitializationUrl,
				requestEntity,
				SSLCommerzPaymentInitResponseDto.class
		);
	}

	private String handlePaymentResponse(ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity, String paymentMethod) {
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			SSLCommerzPaymentInitResponseDto body = responseEntity.getBody();
			if ("FAILED".equalsIgnoreCase(body.getStatus())) {
				throw new SSLCommerzPaymentInitializationException("Payment failed: " + body.getFailedreason());
			}
			return extractRedirectUrl(body, paymentMethod);
		} else {
			throw new SSLCommerzPaymentInitializationException("Failed to send payment request: " + responseEntity.toString());
		}
	}


	public boolean orderValidate(
			String merchantTrnxnId,
			String merchantTrnxnAmount,
			String merchantTrnxnCurrency,
			Map<String, String> requestParameters)
			throws IOException, NoSuchAlgorithmException {

		if (Boolean.FALSE.equals(ipnHashVerify(requestParameters))) {
			this.error = "Unable to verify hash";
			return false;
		}

		String validUrl = buildValidationUrl(requestParameters);
		String json = SSLCommerzUtil.getByOpeningJavaUrlConnection(validUrl);

		if (json.isEmpty()) {
			this.error = "Unable to get Transaction JSON status";
			return false;
		}

		SSLCommerzValidatorResponse resp = SSLCommerzUtil.extractValidatorResponse(json);
		if (!isStatusValid(resp)) {
			this.error = "This transaction is either expired or failed";
			return false;
		}

		if (isTransactionMatching(resp, merchantTrnxnId, merchantTrnxnAmount, merchantTrnxnCurrency)) {
			return true;
		} else {
			this.error = "Currency Amount not matching";
			return false;
		}
	}

	private String buildValidationUrl(Map<String, String> params) {
		String encodedValID = URLEncoder.encode(params.get("val_id"), StandardCharsets.UTF_8);
		String encodedStoreID = URLEncoder.encode(this.storeId, StandardCharsets.UTF_8);
		String encodedStorePassword = URLEncoder.encode(this.storePasswd, StandardCharsets.UTF_8);

		return this.sslczURL
				+ this.validationURL
				+ "?val_id=" + encodedValID
				+ "&store_id=" + encodedStoreID
				+ "&store_passwd=" + encodedStorePassword
				+ "&v=1&format=json";
	}

	public boolean isStatusValid(SSLCommerzValidatorResponse resp) {
		String status = resp.getStatus();
		return "VALID".equals(status) || "VALIDATED".equals(status);
	}

	private boolean isTransactionMatching(
			SSLCommerzValidatorResponse resp,
			String merchantTrnxnId,
			String merchantTrnxnAmount,
			String merchantTrnxnCurrency) {

		return merchantTrnxnId.equals(resp.getTranId())
				&& (Math.abs(Double.parseDouble(merchantTrnxnAmount) - Double.parseDouble(resp.getCurrencyAmount()))
				< 1)
				&& merchantTrnxnCurrency.equals(resp.getCurrencyType());
	}

	public Boolean ipnHashVerify(final Map<String, String> requestParameters)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {

		if (!hasRequiredParams(requestParameters)) {
			return false;
		}

		final String verifyKey = requestParameters.get(VERIFY_KEY);
		if (verifyKey.isEmpty()) {
			return false;
		}

		final TreeMap<String, String> sortedParams = buildSortedParams(requestParameters, verifyKey);
		final String hashString = buildHashString(sortedParams);
		generateHash = this.md5(hashString);

		return generateHash.equals(requestParameters.get(VERIFY_SIGN));
	}


	private boolean hasRequiredParams(Map<String, String> params) {
		return params.containsKey(VERIFY_SIGN) && !params.get(VERIFY_SIGN).isEmpty()
				&& params.containsKey(VERIFY_KEY) && !params.get(VERIFY_KEY).isEmpty();
	}

	private TreeMap<String, String> buildSortedParams(Map<String, String> params, String verifyKey)
			throws NoSuchAlgorithmException {

		final String[] keyList = verifyKey.split(",");
		final TreeMap<String, String> sortedMap = new TreeMap<>();

		for (String key : keyList) {
			sortedMap.put(key, params.get(key));
		}

		final String hashedPass = this.md5(this.storePasswd);
		sortedMap.put("store_passwd", hashedPass);

		return sortedMap;
	}

	private String buildHashString(TreeMap<String, String> sortedParams)
			throws UnsupportedEncodingException {

		String hashString = getParamsString(sortedParams, false) + "&";
		return hashString.substring(0, hashString.length() - 1); // remove trailing '&'
	}


	@SuppressWarnings("squid:S4790")
	String md5(String s) throws NoSuchAlgorithmException {
		byte[] bytesOfMessage = s.getBytes(StandardCharsets.UTF_8);
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] theDigest = md.digest(bytesOfMessage);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < theDigest.length; ++i) {
			sb.append(Integer.toHexString((theDigest[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	public static String getParamsString(Map<String, String> params, boolean urlEncode)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (urlEncode) result.append(URLEncoder.encode(entry.getKey(), UTF_8));
			else result.append(entry.getKey());

			result.append("=");
			if (urlEncode) result.append(URLEncoder.encode(entry.getValue(), UTF_8));
			else result.append(entry.getValue());
			result.append("&");
		}

		String resultString = result.toString();
		return !resultString.isEmpty() ? resultString.substring(0, resultString.length() - 1) : resultString;
	}
}
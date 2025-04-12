package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzPaymentInitResponseDto;
import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzValidatorResponse;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.util.SSLCommerzUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
	private RestTemplate restTemplate;
	private PaymentRepository paymentRepository;

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

	private String[] keyList;
	String generateHash;
	String error;

	@Value("${sslcommerz.validation-url}")
	String sslczURL;

	private String validationURL = "/validator/api/validationserverAPI.php";

	public SSLCommerzPaymentService(RestTemplate restTemplate, PaymentRepository paymentRepository) {
		this.restTemplate = restTemplate;
		this.paymentRepository = paymentRepository;
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
		// Prepare form data as MultiValueMap
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("store_id", this.storeId);
		formData.add("store_passwd", this.storePasswd);
		formData.add("total_amount", parcelBookingRequestDTO.getPrice().toString());
		formData.add("currency", "BDT");
		formData.add("tran_id", parcelBookingRequestDTO.getTransactionId());
		formData.add("success_url", baseUrl + successUrl);
		formData.add("fail_url", baseUrl + failureUrl);
		formData.add("cancel_url", baseUrl + errorUrl);
		formData.add("cus_name", sender.getFullName());
		formData.add("cus_email", sender.getEmail());
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
		formData.add("product_name", parcelBookingRequestDTO.getCategoryId().toString());
		formData.add("product_category", parcelBookingRequestDTO.getCategoryId().toString());
		formData.add("product_profile", "general");

		// Set HTTP headers for form data
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Wrap the data and headers in an HttpEntity
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

		// Send the POST request
		ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity = restTemplate.postForEntity(
				paymentInitializationUrl, requestEntity, SSLCommerzPaymentInitResponseDto.class);

		// Process the response and return a matching JSON response
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			HashMap<Object, Object> responseBody = new HashMap<>();
			responseBody.put("message", "Payment request sent successfully");
			responseBody.put("response", responseEntity.getBody());
			if (responseEntity.getBody().getStatus().equals("FAILED")) {
				responseBody.put("error", responseEntity.getBody().getFailedreason());
			}
			return extractRedirectUrl(responseEntity.getBody(), parcelBookingRequestDTO.getPaymentMethod());
		} else {
			HashMap<Object, Object> errorBody = new HashMap<>();
			errorBody.put("error", "Failed to send payment request");
			errorBody.put("details", responseEntity.getBody());
			throw new RuntimeException(errorBody.toString());
		}
	}

	public boolean orderValidate(
			String merchantTrnxnId,
			String merchantTrnxnAmount,
			String merchantTrnxnCurrency,
			Map<String, String> requestParameters)
			throws IOException, NoSuchAlgorithmException {
		boolean hash_verified = this.ipnHashVerify(requestParameters);
		if (hash_verified) {

			String EncodedValID = URLEncoder.encode(
					requestParameters.get("val_id"), Charset.forName("UTF-8").displayName());
			String EncodedStoreID =
					URLEncoder.encode(this.storeId, Charset.forName("UTF-8").displayName());
			String EncodedStorePassword =
					URLEncoder.encode(this.storePasswd, Charset.forName("UTF-8").displayName());

			// GET Request
			String validUrl = this.sslczURL
					+ this.validationURL
					+ "?val_id="
					+ EncodedValID
					+ "&store_id="
					+ EncodedStoreID
					+ "&store_passwd="
					+ EncodedStorePassword
					+ "&v=1&format=json";
			String json = SSLCommerzUtil.getByOpeningJavaUrlConnection(validUrl);

			if (!json.isEmpty()) {
				SSLCommerzValidatorResponse resp = SSLCommerzUtil.extractValidatorResponse(json);

				if (resp.getStatus().equals("VALID") || resp.getStatus().equals("VALIDATED")) {

					if (merchantTrnxnId.equals(resp.getTranId())
							&& (Math.abs(Double.parseDouble(merchantTrnxnAmount)
											- Double.parseDouble(resp.getCurrencyAmount()))
									< 1)
							&& merchantTrnxnCurrency.equals(resp.getCurrencyType())) {
						return true;
					} else {
						this.error = "Currency Amount not matching";
						return false;
					}

				} else {
					this.error = "This transaction is either expired or failed";
					return false;
				}
			} else {
				this.error = "Unable to get Transaction JSON status";
				return false;
			}
		} else {
			this.error = "Unable to verify hash";
			return false;
		}
	}

	Boolean ipnHashVerify(final Map<String, String> requestParameters)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {

		// Check For verify_sign and verify_key parameters
		if (!requestParameters.get("verify_sign").isEmpty()
				&& !requestParameters.get("verify_key").isEmpty()) {
			// Get the verify key
			String verify_key = requestParameters.get("verify_key");
			if (!verify_key.isEmpty()) {

				// Split key String by comma to make a list array
				keyList = verify_key.split(",");
				TreeMap<String, String> sortedMap = new TreeMap<String, String>();

				// Store key and value of post in a sorted Map
				for (final String k : keyList) {
					sortedMap.put(k, requestParameters.get(k));
				}

				// Store Hashed Password in list
				final String hashedPass = this.md5(this.storePasswd);
				sortedMap.put("store_passwd", hashedPass);
				// Concat and make String from array
				String hashString = "";
				hashString += this.getParamsString(sortedMap, false) + "&";

				// Trim '&' from end of this String
				hashString = hashString.substring(0, hashString.length() - 1); // omitting last &

				// Make hash by hash_string and store
				generateHash = this.md5(hashString);

				// Check if generated hash and verify_sign match or not
				// Matched
				return generateHash.equals(requestParameters.get("verify_sign"));
			}

			return false;
		} else {
			return false;
		}
	}

	@SuppressWarnings("squid:S4790")
	String md5(String s) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytesOfMessage = s.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] theDigest = md.digest(bytesOfMessage);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < theDigest.length; ++i) {
			sb.append(Integer.toHexString((theDigest[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	public static String getParamsString(Map<String, String> params, boolean urlEncode)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (urlEncode) result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			else result.append(entry.getKey());

			result.append("=");
			if (urlEncode) result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			else result.append(entry.getValue());
			result.append("&");
		}

		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}
}

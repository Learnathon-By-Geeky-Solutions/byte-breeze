package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzPaymentInitResponseDto;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
@Service
public class SSLCommerzPaymentService implements PaymentService {
    private RestTemplate restTemplate;

    @Value("${sslcommerz.store-id}")
    String storeId;

    @Value("${sslcommerz.store-passwd}")
    String storePasswd;

    @Value("${sslcommerz.init-url}")
    String paymentInitializationUrl;

    public SSLCommerzPaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String extractRedirectUrl(SSLCommerzPaymentInitResponseDto sslCommerzPaymentInitResponseDto, String paymentMethod) {
        return sslCommerzPaymentInitResponseDto.getDesc()
                .stream()
                .filter(desc -> desc.getName().equalsIgnoreCase(paymentMethod))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment method not found"))
                .getRedirectGatewayURL();
    }

    @Override
    public String getPaymentUrl(ParcelBookingRequestDTO parcelBookingRequestDTO) {
        // Prepare form data as MultiValueMap
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("store_id", this.storeId);
        formData.add("store_passwd", this.storePasswd);
        formData.add("total_amount", parcelBookingRequestDTO.getPrice().toString());
        formData.add("currency", "BDT");
        formData.add("tran_id", parcelBookingRequestDTO.getTransactionId());
        formData.add("success_url", "https://yourdomain.com/success");
        formData.add("fail_url", "https://yourdomain.com/fail");
        formData.add("cancel_url", "https://yourdomain.com/cancel");
        formData.add("cus_name", "Khairul Islam");
        formData.add("cus_email", "suvashkumarsumon@gmail.com");
        formData.add("cus_add1", "Binodpur, Rajshahi");
        formData.add("cus_city", "Rajshahi");
        formData.add("cus_state", "Rajshahi");
        formData.add("cus_postcode", "6205");
        formData.add("cus_country", "Bangladesh");
        formData.add("cus_phone", "01700000000");
        formData.add("ship_name", "Khairul Islam");
        formData.add("ship_add1", "Binodpur, Rajshahi");
        formData.add("ship_city", "Rajshahi");
        formData.add("ship_state", "Rajshahi");
        formData.add("ship_postcode", "6205");
        formData.add("ship_country", "Bangladesh");
        formData.add("multi_card_name", "visacard");
        formData.add("value_a", "ref001");
        formData.add("value_b", "ref002");
        formData.add("value_c", "ref003");
        formData.add("value_d", "ref004");
        formData.add("product_name", parcelBookingRequestDTO.getCategoryId().toString());
        formData.add("product_category", parcelBookingRequestDTO.getCategoryId().toString());
        formData.add("product_profile", "general");

        // Set HTTP headers for form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Wrap the data and headers in an HttpEntity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Send the POST request
        ResponseEntity<SSLCommerzPaymentInitResponseDto> responseEntity = restTemplate.postForEntity(paymentInitializationUrl, requestEntity, SSLCommerzPaymentInitResponseDto.class);

        // Process the response and return a matching JSON response
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            HashMap<Object, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Payment request sent successfully");
            responseBody.put("response", responseEntity.getBody());
            if(responseEntity.getBody().getStatus().equals("FAILED")) {
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

}

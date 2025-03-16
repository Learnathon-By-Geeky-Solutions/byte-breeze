package com.bytebreeze.quickdrop.controller.api;

import com.bytebreeze.quickdrop.service.SSLCommerzPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sslcommerz")
public class SSLCommerzPaymentApiController {

    @Value("${sslcommerz.store-passwd}")
    private String storePassword;

    SSLCommerzPaymentService sslCommerzPaymentService;

    public SSLCommerzPaymentApiController(SSLCommerzPaymentService sslCommerzPaymentService) {
        this.sslCommerzPaymentService = sslCommerzPaymentService;
    }

    @PostMapping("/success")
    public ResponseEntity<Map<String, Object>> handlePaymentSuccess(@RequestParam Map<String, String> paramMap) throws IOException, NoSuchAlgorithmException {
        // Extract transaction details
        String transactionId = paramMap.get("tran_id");
        String amount = paramMap.get("amount");
        String currency = paramMap.get("currency");
        String paymentMethod = paramMap.get("card_type");
        String bankTransactionId = paramMap.get("bank_tran_id");
        String storeAmount = paramMap.get("store_amount");
        String cardIssuer = paramMap.get("card_issuer");
        String riskLevel = paramMap.get("risk_level");
        String riskTitle = paramMap.get("risk_title");

        System.out.println(paramMap.toString());

        String marchentTransactionId = "";
        String marchentTransactionAmount = "";
        String marchentTransactionCurrency = "BDT";


        // Verify payment
        if(!sslCommerzPaymentService.orderValidate(marchentTransactionId, marchentTransactionAmount, marchentTransactionCurrency, paramMap)) {
            System.out.println("Payment verification failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "Payment verification failed"));
        }

        System.out.println("Payment verification successful");

        // Construct response map
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment verification successful");
        response.put("transaction_id", transactionId);
        response.put("amount", amount);
        response.put("currency", currency);
        response.put("payment_method", paymentMethod);
        response.put("bank_transaction_id", bankTransactionId);
        response.put("store_amount", storeAmount);
        response.put("card_issuer", cardIssuer);
        response.put("risk_level", riskLevel);
        response.put("risk_title", riskTitle);

        return ResponseEntity.ok(response);
    }
}

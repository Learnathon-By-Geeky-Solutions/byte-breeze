package com.bytebreeze.quickdrop.controller.api;

import com.bytebreeze.quickdrop.enums.PaymentStatus;
import com.bytebreeze.quickdrop.model.Payment;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.service.SSLCommerzPaymentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sslcommerz")
public class SSLCommerzPaymentApiController {

    private final PaymentRepository paymentRepository;
    @Value("${sslcommerz.store-passwd}")
    private String storePassword;

    SSLCommerzPaymentService sslCommerzPaymentService;

    public SSLCommerzPaymentApiController(SSLCommerzPaymentService sslCommerzPaymentService, PaymentRepository paymentRepository) {
        this.sslCommerzPaymentService = sslCommerzPaymentService;
        this.paymentRepository = paymentRepository;
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

        // fetch payment information from database
        Payment payment = paymentRepository.findByTransactionId(transactionId).orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));
        String marchentTransactionId = payment.getTransactionId();
        String marchentTransactionAmount = payment.getAmount().toString();
        String marchentTransactionCurrency = payment.getCurrency();


        // Verify payment
        if(!sslCommerzPaymentService.orderValidate(marchentTransactionId, marchentTransactionAmount, marchentTransactionCurrency, paramMap)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "Payment verification failed"));
        }

        // Update payment status
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

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

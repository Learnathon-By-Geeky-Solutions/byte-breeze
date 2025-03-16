package com.bytebreeze.quickdrop.controller.api;

import com.bytebreeze.quickdrop.enums.PaymentStatus;
import com.bytebreeze.quickdrop.model.Payment;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.service.SSLCommerzPaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
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
    public String handlePaymentSuccess(@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws IOException, NoSuchAlgorithmException {
        // Extract transaction details
        String transactionId = paramMap.get("tran_id");

        // fetch payment information from database
        Payment payment = paymentRepository.findByTransactionId(transactionId).orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));
        String marchentTransactionId = payment.getTransactionId();
        String marchentTransactionAmount = payment.getAmount().toString();
        String marchentTransactionCurrency = payment.getCurrency();


        // Verify payment
        if(!sslCommerzPaymentService.orderValidate(marchentTransactionId, marchentTransactionAmount, marchentTransactionCurrency, paramMap)) {
            redirectAttributes.addFlashAttribute("error", "Payment verification failed.");
            return "redirect:/user/dashboard";
        }

        // Update payment status
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        redirectAttributes.addFlashAttribute("success", "Parcel booked successfully. Relax and wait for the delivery.");
        return "redirect:/user/dashboard";
    }
}

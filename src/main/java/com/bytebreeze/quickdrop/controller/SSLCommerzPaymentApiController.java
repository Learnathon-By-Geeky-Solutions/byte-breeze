package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.enums.PaymentStatus;
import com.bytebreeze.quickdrop.model.Payment;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.repository.PaymentRepository;
import com.bytebreeze.quickdrop.service.SSLCommerzPaymentService;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sslcommerz")
public class SSLCommerzPaymentApiController {

	private final PaymentRepository paymentRepository;
	private final ParcelRepository parcelRepository;

	@Value("${sslcommerz.store-passwd}")
	private String storePassword;

	private final SSLCommerzPaymentService sslCommerzPaymentService;

	public SSLCommerzPaymentApiController(
			SSLCommerzPaymentService sslCommerzPaymentService,
			PaymentRepository paymentRepository,
			ParcelRepository parcelRepository) {
		this.sslCommerzPaymentService = sslCommerzPaymentService;
		this.paymentRepository = paymentRepository;
		this.parcelRepository = parcelRepository;
	}

	@PostMapping("/success")
	public String handlePaymentSuccess(
			@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes)
			throws IOException, NoSuchAlgorithmException {
		// Extract transaction details
		String transactionId = paramMap.get("tran_id");

		// fetch payment information from database
		Payment payment = paymentRepository
				.findByTransactionId(transactionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));
		String marchentTransactionId = payment.getTransactionId();
		String marchentTransactionAmount = payment.getAmount().toString();
		String marchentTransactionCurrency = payment.getCurrency();

		// Verify payment
		if (!sslCommerzPaymentService.orderValidate(
				marchentTransactionId, marchentTransactionAmount, marchentTransactionCurrency, paramMap)) {
			redirectAttributes.addFlashAttribute("error", "Payment verification failed.");
			return "redirect:/user/dashboard";
		}

		// Update payment status
		payment.setPaymentStatus(PaymentStatus.SUCCESS);
		paymentRepository.save(payment);

		redirectAttributes.addFlashAttribute("success", "Parcel booked successfully. Relax and wait for the delivery.");
		return "redirect:/user/dashboard";
	}

	@PostMapping("/failure")
	public String handlePaymentFailure(
			@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		// Extract transaction details
		String transactionId = paramMap.get("tran_id");

		// fetch payment information from database
		Payment payment = paymentRepository
				.findByTransactionId(transactionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));

		// Update payment status
		payment.setPaymentStatus(PaymentStatus.FAILED);
		paymentRepository.save(payment);

		redirectAttributes.addFlashAttribute("error", "Booking failed. Please try again.");
		return "redirect:/user/dashboard";
	}

	@PostMapping("/cancel")
	public String handlePaymentCancel(
			@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		// Extract transaction details
		String transactionId = paramMap.get("tran_id");

		// fetch payment information from database
		Payment payment = paymentRepository
				.findByTransactionId(transactionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID"));

		// Delete the booking from database along with payment information
		UUID parcelId = payment.getParcel().getId();
		paymentRepository.delete(payment);
		parcelRepository.deleteById(parcelId);

		redirectAttributes.addFlashAttribute("error", "Booking Canceled.");
		return "redirect:/user/dashboard";
	}
}

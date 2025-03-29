package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.request.CalculateShippingCostRequestDto;
import com.bytebreeze.quickdrop.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class ParcelShippingCostController {
	public final ParcelService parcelService;

	public ParcelShippingCostController(ParcelService parcelService) {
		this.parcelService = parcelService;
	}

	@PostMapping("/shipping-cost")
	public ResponseEntity<?> calculateShippingCost(
			@Valid @RequestBody CalculateShippingCostRequestDto calculateShippingCostRequestDto) {
		return ResponseEntity.ok().body(parcelService.calculateShippingCost(calculateShippingCostRequestDto));
	}
}

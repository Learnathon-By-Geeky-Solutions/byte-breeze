package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.request.CalculateShippingCostRequestDto;
import com.bytebreeze.quickdrop.service.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parcel")
@RequiredArgsConstructor
public class ParcelShippingCostController {
	public final ParcelService parcelService;

	@PostMapping("/shipping-cost")
	public ResponseEntity<Double> calculateShippingCost(
			@Valid @RequestBody CalculateShippingCostRequestDto calculateShippingCostRequestDto) {
		return ResponseEntity.ok().body(parcelService.calculateShippingCost(calculateShippingCostRequestDto));
	}
}

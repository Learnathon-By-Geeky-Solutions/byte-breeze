package com.bytebreeze.quickdrop.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.request.CalculateShippingCostRequestDto;
import com.bytebreeze.quickdrop.service.ParcelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ParcelShippingCostControllerTest {

	@Mock
	private ParcelService parcelService;

	private ParcelShippingCostController controller;

	@BeforeEach
	void setUp() {
		controller = new ParcelShippingCostController(parcelService);
	}

	@Test
	void calculateShippingCost_shouldReturnExpectedCost() {
		CalculateShippingCostRequestDto dto = new CalculateShippingCostRequestDto(10.0, 5.0);
		double expectedCost = 107.0;
		when(parcelService.calculateShippingCost(dto)).thenReturn(expectedCost);

		ResponseEntity<Double> response = controller.calculateShippingCost(dto);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedCost, response.getBody());
		verify(parcelService, times(1)).calculateShippingCost(dto);
	}

	@Test
	void calculateShippingCost_withZeroSizeAndWeight_shouldReturnBaseCharge() {
		CalculateShippingCostRequestDto dto = new CalculateShippingCostRequestDto(0.0, 0.0);
		double expectedCost = 100.0; // only delivery charge

		when(parcelService.calculateShippingCost(dto)).thenReturn(expectedCost);

		ResponseEntity<Double> response = controller.calculateShippingCost(dto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedCost, response.getBody());
	}

	@Test
	void calculateShippingCost_withLargeValues_shouldReturnExpectedCost() {
		CalculateShippingCostRequestDto dto = new CalculateShippingCostRequestDto(1000.0, 500.0);
		double expectedCost = 0.2 * 1000 + 0.4 * 500 + 100; // 200 + 200 + 100 = 500

		when(parcelService.calculateShippingCost(dto)).thenReturn(expectedCost);

		ResponseEntity<Double> response = controller.calculateShippingCost(dto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedCost, response.getBody());
	}

	@Test
	void calculateShippingCost_shouldThrowException_whenServiceFails() {
		CalculateShippingCostRequestDto dto = new CalculateShippingCostRequestDto(10.0, 5.0);
		when(parcelService.calculateShippingCost(dto)).thenThrow(new RuntimeException("Internal error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			controller.calculateShippingCost(dto);
		});

		assertEquals("Internal error", exception.getMessage());
	}
}

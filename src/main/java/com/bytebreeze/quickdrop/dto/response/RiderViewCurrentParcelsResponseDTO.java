package com.bytebreeze.quickdrop.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class RiderViewCurrentParcelsResponseDTO {

	private UUID id;
	private String trackingId;
	private String pickupDistrict;
	private String pickupUpazila;
	private String pickupVillage;
	private String receiverDistrict;
	private String receiverUpazila;
	private String receiverVillage;
	private BigDecimal price;
}

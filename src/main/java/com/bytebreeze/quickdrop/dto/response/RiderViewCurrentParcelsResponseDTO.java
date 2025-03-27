package com.bytebreeze.quickdrop.dto.response;


import com.bytebreeze.quickdrop.model.ProductCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RiderViewCurrentParcelsResponseDTO {

    private UUID id;
    private String trackingId;


    // private ProductCategory category;
    private String pickupDistrict;
    private String pickupUpazila;
    private String pickupVillage;

    private String receiverDistrict;
    private String receiverUpazila;
    private String receiverVillage;
    private BigDecimal price;



}

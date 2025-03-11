package com.bytebreeze.quickdrop.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ParcelBookingRequestDTO {

    @NotNull
    private UUID categoryId;

    private String description;

    @NotNull
    private Double weight;

    @NotNull
    private String size;

    @NotNull
    private String receiverName;

    @NotNull
    private String receiverPhone;

    private String receiverEmail;

    @NotNull
    private String receiverAddress;

    private String receiverOtp;

    @NotNull
    private Long senderId;

    private Long riderId;

    private String riderOtp;

    private LocalDateTime pickupTime;

    private LocalDateTime deliveryTime;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Double distance;
}

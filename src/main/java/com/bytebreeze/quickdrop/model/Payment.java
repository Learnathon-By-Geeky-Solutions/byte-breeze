package com.bytebreeze.quickdrop.model;

import com.bytebreeze.quickdrop.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String bankTransactionId;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    private Parcel parcel;

    private PaymentStatus paymentStatus;


}

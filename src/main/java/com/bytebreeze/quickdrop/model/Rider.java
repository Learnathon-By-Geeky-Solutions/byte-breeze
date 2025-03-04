package com.bytebreeze.quickdrop.model;
import com.bytebreeze.quickdrop.enums.Gender;
import com.bytebreeze.quickdrop.enums.VehicleType;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "riders")
@PrimaryKeyJoinColumn(name = "id")

public class Rider extends User {


    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String upazila;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String postalCode;





    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private String vehicleModel;

    @Column(nullable = false)
    private String vehicleRegistrationNumber;

    @Column(nullable = false)
    private String vehicleInsuranceProvider;

    @Column(nullable = true)
    private LocalDate insuranceExpiryDate;

    @Column(nullable = true)
    private String vehicleRegistrationDocumentPath;

    @Column(nullable = true)
    private String insuranceDocumentPath;




    @Column(nullable = false, unique = true)
    private String nationalIdNumber;

    @Column(nullable = true)
    private String nationalIdFrontPath;

    @Column(nullable = true)
    private String nationalIdBackPath;

    @Column(nullable = true)
    private String driversLicenseNumber;

    @Column(nullable = true)
    private LocalDate licenseExpiryDate;

    @Column(nullable = true)
    private String driversLicenseFrontPath;

    @Column(nullable = true)
    private String driversLicenseBackPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(nullable = true)
    private Boolean wantToDelivery = false;

    @Column(nullable = true)
    private double riderAvgRating = 0.0;

    @Column(nullable = true)
    private int totalRating = 0;

    @Column(nullable = true)
    private double riderBalance = 0.0;
}

package com.bytebreeze.quickdrop.dto;

import com.bytebreeze.quickdrop.enums.Gender;
import com.bytebreeze.quickdrop.enums.VehicleType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class RiderOnboardingDTO {

    // Personal Info
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String upazila;
    private String district;
    private String postalCode;

    // Vehicle Info
    private VehicleType vehicleType;
    private String vehicleModel;
    private String vehicleRegistrationNumber;
    private String vehicleInsuranceProvider;
    private LocalDate insuranceExpiryDate;
    private LocalDate licenseExpiryDate;

    private String nationalIdNumber;
    private String driversLicenseNumber;



    // File Uploads
    private MultipartFile vehicleRegistrationDocument;
    private MultipartFile insuranceDocument;
    private MultipartFile nationalIdFront;
    private MultipartFile nationalIdBack;
    private MultipartFile driversLicenseFront;
    private MultipartFile driversLicenseBack;



}

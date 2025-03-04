package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
//import com.bytebreeze.quickdrop.mapper.OnboardRiderMapper;
import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RiderService {

    private final RiderRepository riderRepository;
    //private final OnboardRiderMapper onboardRiderMapper;
    private final FileStorageService fileStorageService;

    public Rider onboardRider(RiderOnboardingDTO riderOnboardingDTO){

        Rider rider = new Rider();

        //rider = onboardRiderMapper.toEntity(riderOnboardingDTO);
        rider.setDateOfBirth(riderOnboardingDTO.getDateOfBirth());
        rider.setGender(riderOnboardingDTO.getGender());
        rider.setAddress(riderOnboardingDTO.getAddress());
        rider.setUpazila(riderOnboardingDTO.getUpazila());
        rider.setDistrict(riderOnboardingDTO.getDistrict());
        rider.setPostalCode(riderOnboardingDTO.getPostalCode());
        rider.setVehicleType(riderOnboardingDTO.getVehicleType());
        rider.setVehicleModel(riderOnboardingDTO.getVehicleModel());
        rider.setVehicleRegistrationNumber(riderOnboardingDTO.getVehicleRegistrationNumber());
        rider.setVehicleInsuranceProvider(riderOnboardingDTO.getVehicleInsuranceProvider());
        rider.setInsuranceExpiryDate(riderOnboardingDTO.getInsuranceExpiryDate());
        rider.setLicenseExpiryDate(riderOnboardingDTO.getLicenseExpiryDate());
        rider.setNationalIdNumber(riderOnboardingDTO.getNationalIdNumber());
        rider.setDriversLicenseNumber(riderOnboardingDTO.getDriversLicenseNumber());



        // Store files & get paths
        rider.setNationalIdFrontPath(fileStorageService.storeFile(riderOnboardingDTO.getNationalIdFront()));
        rider.setNationalIdBackPath(fileStorageService.storeFile(riderOnboardingDTO.getNationalIdBack()));
        rider.setDriversLicenseFrontPath(fileStorageService.storeFile(riderOnboardingDTO.getDriversLicenseFront()));
        rider.setDriversLicenseBackPath(fileStorageService.storeFile(riderOnboardingDTO.getDriversLicenseBack()));
        rider.setVehicleRegistrationDocumentPath(fileStorageService.storeFile(riderOnboardingDTO.getVehicleRegistrationDocument()));
        rider.setInsuranceDocumentPath(fileStorageService.storeFile(riderOnboardingDTO.getInsuranceDocument()));

        // Set verification status
        rider.setVerificationStatus(VerificationStatus.PENDING);



        return riderRepository.save(rider);
    }


}

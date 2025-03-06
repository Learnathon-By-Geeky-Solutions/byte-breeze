package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
//import com.bytebreeze.quickdrop.mapper.OnboardRiderMapper;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.custom.UserNotFoundException;
import com.bytebreeze.quickdrop.mapper.RegisterRiderMapper;
import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.RiderRepository;
import com.bytebreeze.quickdrop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RiderService {

    private final RiderRepository riderRepository;
    //private final OnboardRiderMapper onboardRiderMapper;
    private final FileStorageService fileStorageService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegisterRiderMapper registerRiderMapper;

    public boolean isEmailAlreadyInUse(String email) {
        return userRepository.findByEmail(email).isPresent();

    }

    public Rider findByRiderId(UUID id) {
        return riderRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Rider registerRider(RiderRegistrationRequestDTO dto){

        if(isEmailAlreadyInUse(dto.getEmail())){

            throw new AlreadyExistsException("Provided email already registered");
        }

        Rider rider = new Rider();

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        rider = registerRiderMapper.toEntity(dto);
        if (rider.getRoles() == null || rider.getRoles().isEmpty()) {
            rider.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_RIDER)));
        }
        return riderRepository.save(rider);
    }


    public Rider onboardRider(UUID riderId, RiderOnboardingDTO riderOnboardingDTO){

        Rider rider = findByRiderId(riderId);

       // rider = onboardRiderMapper.toEntity(riderOnboardingDTO);

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

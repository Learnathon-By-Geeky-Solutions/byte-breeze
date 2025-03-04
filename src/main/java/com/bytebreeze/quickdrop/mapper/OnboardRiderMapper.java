package com.bytebreeze.quickdrop.mapper;

import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.model.Rider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OnboardRiderMapper {
    OnboardRiderMapper INSTANCE = Mappers.getMapper(OnboardRiderMapper.class);

    // Map non-MultipartFile fields from DTO to Entity
    @Mapping(target = "id", ignore = true) // Ignore ID as it is auto-generated
    @Mapping(target = "vehicleRegistrationDocumentPath", ignore = true)
    @Mapping(target = "insuranceDocumentPath", ignore = true)
    @Mapping(target = "nationalIdFrontPath", ignore = true)
    @Mapping(target = "nationalIdBackPath", ignore = true)
    @Mapping(target = "driversLicenseFrontPath", ignore = true)
    @Mapping(target = "driversLicenseBackPath", ignore = true)
    @Mapping(target = "verificationStatus", ignore = true) // Set default value in entity
    @Mapping(target = "wantToDelivery", ignore = true)
    @Mapping(target = "riderAvgRating", ignore = true)
    @Mapping(target = "totalRating", ignore = true)
    @Mapping(target = "riderBalance", ignore = true)

    // Ignore fields inherited from User
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    Rider toEntity(RiderOnboardingDTO riderOnboardingDTO);

}

package com.bytebreeze.quickdrop.mapper;

import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.model.Rider;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OnboardRiderMapper {

    Rider toEntity(RiderOnboardingDTO riderOnboardingDTO);

}

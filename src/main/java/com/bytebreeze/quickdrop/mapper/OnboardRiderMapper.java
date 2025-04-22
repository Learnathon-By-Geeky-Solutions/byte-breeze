package com.bytebreeze.quickdrop.mapper;

import com.bytebreeze.quickdrop.dto.request.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.entity.Rider;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OnboardRiderMapper {

	Rider toEntity(RiderOnboardingDTO riderOnboardingDTO);
}

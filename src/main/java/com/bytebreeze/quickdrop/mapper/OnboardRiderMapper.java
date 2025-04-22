package com.bytebreeze.quickdrop.mapper;

import com.bytebreeze.quickdrop.dto.request.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.entity.RiderEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OnboardRiderMapper {

	RiderEntity toEntity(RiderOnboardingDTO riderOnboardingDTO);
}

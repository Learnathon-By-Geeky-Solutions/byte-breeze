package com.bytebreeze.quickdrop.mapper;

import com.bytebreeze.quickdrop.dto.request.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.entity.RiderEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface RegisterRiderMapper {

	RiderEntity toEntity(RiderRegistrationRequestDTO riderRegistrationRequestDTO);
}

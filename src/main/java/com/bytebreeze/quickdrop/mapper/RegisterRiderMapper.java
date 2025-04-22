package com.bytebreeze.quickdrop.mapper;

import com.bytebreeze.quickdrop.dto.request.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.model.Rider;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface RegisterRiderMapper {

	Rider toEntity(RiderRegistrationRequestDTO riderRegistrationRequestDTO);
}

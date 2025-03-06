package com.bytebreeze.quickdrop.mapper;


import com.bytebreeze.quickdrop.dto.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.model.Rider;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RegisterRiderMapper {

    Rider toEntity(RiderRegistrationRequestDTO riderRegistrationRequestDTO);

}

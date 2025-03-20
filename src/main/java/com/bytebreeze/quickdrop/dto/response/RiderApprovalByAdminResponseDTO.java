package com.bytebreeze.quickdrop.dto.response;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class RiderApprovalByAdminResponseDTO {

    private UUID id;

    private String fullName;

    private String email;

    private String phoneNumber;

    public RiderApprovalByAdminResponseDTO(UUID id, String fullName, String email, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}

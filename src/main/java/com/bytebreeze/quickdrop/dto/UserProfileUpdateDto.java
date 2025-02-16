package com.bytebreeze.quickdrop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserProfileUpdateDto {
    @NotEmpty(message = "Name must not be empty.")
    private String fullName;

    @NotEmpty(message = "Password must not be empty.")
    private String password;

    private String phoneNumber;
}

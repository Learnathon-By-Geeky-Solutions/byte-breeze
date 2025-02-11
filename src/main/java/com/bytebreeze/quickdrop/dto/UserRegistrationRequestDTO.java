package com.bytebreeze.quickdrop.dto;


//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class UserRegistrationRequestDTO {

    @NotBlank(message = "Name can not be empty")
    private String fullName;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid Email format")
    private String email;

    @NotBlank(message = "Password can not be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long" )
    private String password;


}

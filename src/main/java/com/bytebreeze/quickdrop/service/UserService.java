package com.bytebreeze.quickdrop.service;


import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public boolean isEmailAlreadyInUse(String email) {
        return userRepository.findByEmail(email).isPresent();

    }


    public String registerUser(UserRegistrationRequestDTO dto){

        if(isEmailAlreadyInUse(dto.getEmail())){

            throw new AlreadyExistsException("Provided email already registered");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }
}

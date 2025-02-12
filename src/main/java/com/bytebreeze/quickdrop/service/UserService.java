package com.bytebreeze.quickdrop.service;


import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public boolean isEmailAlreadyInUse(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            return true;
        }
        return false;
    }


    public String registerUser(UserRegistrationRequestDTO dto){

        if(isEmailAlreadyInUse(dto.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }
}

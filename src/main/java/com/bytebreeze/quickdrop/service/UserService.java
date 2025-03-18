package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
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

  public String registerUser(UserRegistrationRequestDTO dto) {

    if (isEmailAlreadyInUse(dto.getEmail())) {

      throw new AlreadyExistsException("Provided email already registered");
    }

    User user = new User();
    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    if (user.getRoles() == null || user.getRoles().isEmpty()) {
      user.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_USER)));
    }
    userRepository.save(user);

    return "User registered successfully";
  }

  public User getAuthenticatedUser() {
    String authenticatedUserEmail = AuthUtil.getAuthenticatedUsername();
    Optional<User> userOptional = userRepository.findByEmail(authenticatedUserEmail);
    return userOptional.orElseThrow(() -> new RuntimeException("User not found"));
  }

  public UserProfileUpdateDto userProfileUpdateGet() {
    User user = this.getAuthenticatedUser();
    UserProfileUpdateDto userProfileUpdateDto = new UserProfileUpdateDto();
    userProfileUpdateDto.setFullName(user.getFullName());
    userProfileUpdateDto.setPassword(user.getPassword());
    userProfileUpdateDto.setPhoneNumber(user.getPhoneNumber());
    userProfileUpdateDto.setProfileImageUrl(user.getProfilePicture());
    return userProfileUpdateDto;
  }

  public boolean updateUserProfile(UserProfileUpdateDto userProfileUpdateDto) {
    User user = this.getAuthenticatedUser();

    if (userProfileUpdateDto.getFullName() != null) {
      user.setFullName(userProfileUpdateDto.getFullName());
    }

    // Only update password if a new, non-empty password is provided
    if (userProfileUpdateDto.getPassword() != null
        && !userProfileUpdateDto.getPassword().trim().isEmpty()) {
      user.setPassword(passwordEncoder.encode(userProfileUpdateDto.getPassword()));
    }

    if (userProfileUpdateDto.getPhoneNumber() != null) {
      user.setPhoneNumber(userProfileUpdateDto.getPhoneNumber());
    }

    userRepository.save(user);
    return true;
  }
}

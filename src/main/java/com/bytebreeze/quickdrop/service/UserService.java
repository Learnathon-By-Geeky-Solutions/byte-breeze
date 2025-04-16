package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.custom.UserNotFoundException;
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
		validateEmailNotInUse(dto.getEmail());

		User user = mapToUserEntity(dto);
		assignDefaultRoleIfEmpty(user);

		userRepository.save(user);
		return "User registered successfully";
	}

	private void validateEmailNotInUse(String email) {
		if (isEmailAlreadyInUse(email)) {
			throw new AlreadyExistsException("Provided email already registered");
		}
	}

	private User mapToUserEntity(UserRegistrationRequestDTO dto) {
		User user = new User();
		user.setFullName(dto.getFullName());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		return user;
	}

	private void assignDefaultRoleIfEmpty(User user) {
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			user.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_USER)));
		}
	}

	public User getAuthenticatedUser() {
		String authenticatedUserEmail = AuthUtil.getAuthenticatedUsername();
		Optional<User> userOptional = userRepository.findByEmail(authenticatedUserEmail);
		return userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));
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

	public boolean updateUserProfile(UserProfileUpdateDto dto) {
		User user = getAuthenticatedUser();

		updateFullName(user, dto);
		updatePassword(user, dto);
		updatePhoneNumber(user, dto);

		userRepository.save(user);
		return true;
	}

	private void updateFullName(User user, UserProfileUpdateDto dto) {
		if (dto.getFullName() != null) {
			user.setFullName(dto.getFullName());
		}
	}

	private void updatePassword(User user, UserProfileUpdateDto dto) {
		String password = dto.getPassword();
		if (password != null && !password.trim().isEmpty()) {
			user.setPassword(passwordEncoder.encode(password));
		}
	}

	private void updatePhoneNumber(User user, UserProfileUpdateDto dto) {
		if (dto.getPhoneNumber() != null) {
			user.setPhoneNumber(dto.getPhoneNumber());
		}
	}
}

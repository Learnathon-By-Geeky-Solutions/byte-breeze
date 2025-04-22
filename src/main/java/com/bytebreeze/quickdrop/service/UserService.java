package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.dto.request.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.dto.request.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.exception.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.UserNotFoundException;
import com.bytebreeze.quickdrop.entity.UserEntity;
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

		UserEntity user = mapToUserEntity(dto);
		assignDefaultRoleIfEmpty(user);

		userRepository.save(user);
		return "User registered successfully";
	}

	private void validateEmailNotInUse(String email) {
		if (isEmailAlreadyInUse(email)) {
			throw new AlreadyExistsException("Provided email already registered");
		}
	}

	private UserEntity mapToUserEntity(UserRegistrationRequestDTO dto) {
		UserEntity user = new UserEntity();
		user.setFullName(dto.getFullName());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		return user;
	}

	private void assignDefaultRoleIfEmpty(UserEntity user) {
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			user.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_USER)));
		}
	}

	public UserEntity getAuthenticatedUser() {
		String authenticatedUserEmail = AuthUtil.getAuthenticatedUsername();
		Optional<UserEntity> userOptional = userRepository.findByEmail(authenticatedUserEmail);
		return userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	public UserProfileUpdateDto userProfileUpdateGet() {
		UserEntity user = this.getAuthenticatedUser();
		UserProfileUpdateDto userProfileUpdateDto = new UserProfileUpdateDto();
		userProfileUpdateDto.setFullName(user.getFullName());
		userProfileUpdateDto.setPassword(user.getPassword());
		userProfileUpdateDto.setPhoneNumber(user.getPhoneNumber());
		userProfileUpdateDto.setProfileImageUrl(user.getProfilePicture());
		return userProfileUpdateDto;
	}

	public boolean updateUserProfile(UserProfileUpdateDto dto) {
		UserEntity user = getAuthenticatedUser();

		updateFullName(user, dto);
		updatePassword(user, dto);
		updatePhoneNumber(user, dto);

		userRepository.save(user);
		return true;
	}

	private void updateFullName(UserEntity user, UserProfileUpdateDto dto) {
		if (dto.getFullName() != null) {
			user.setFullName(dto.getFullName());
		}
	}

	private void updatePassword(UserEntity user, UserProfileUpdateDto dto) {
		String password = dto.getPassword();
		if (password != null && !password.trim().isEmpty()) {
			user.setPassword(passwordEncoder.encode(password));
		}
	}

	private void updatePhoneNumber(UserEntity user, UserProfileUpdateDto dto) {
		if (dto.getPhoneNumber() != null) {
			user.setPhoneNumber(dto.getPhoneNumber());
		}
	}
}

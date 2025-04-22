package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.request.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.dto.request.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.enums.Role;
import com.bytebreeze.quickdrop.exception.custom.AlreadyExistsException;
import com.bytebreeze.quickdrop.exception.custom.UserNotFoundException;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.util.AuthUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User user;
	private String email = "test@example.com";
	private MockedStatic<AuthUtil> authUtilMockedStatic;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(UUID.randomUUID());
		user.setFullName("Test User");
		user.setEmail(email);
		user.setPassword("encodedPassword");
		user.setPhoneNumber("1234567890");
		user.setProfilePicture("http://example.com/profile.jpg");
		user.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_USER)));

		// Initialize static mock for AuthUtil
		authUtilMockedStatic = mockStatic(AuthUtil.class);
	}

	@AfterEach
	void tearDown() {
		// Close the static mock after each test
		authUtilMockedStatic.close();
	}

	@Test
	void testIsEmailAlreadyInUse_EmailExists() {
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		assertTrue(userService.isEmailAlreadyInUse(email));
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	void testIsEmailAlreadyInUse_EmailDoesNotExist() {
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
		assertFalse(userService.isEmailAlreadyInUse(email));
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	void testRegisterUser_Success() {
		UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO();
		dto.setFullName("New User");
		dto.setEmail(email);
		dto.setPassword("password123");

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		String result = userService.registerUser(dto);

		assertEquals("User registered successfully", result);
		verify(userRepository, times(1)).findByEmail(email);
		verify(passwordEncoder, times(1)).encode("password123");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testRegisterUser_EmailAlreadyInUse() {
		UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO();
		dto.setEmail(email);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		assertThrows(AlreadyExistsException.class, () -> userService.registerUser(dto));
		verify(userRepository, times(1)).findByEmail(email);
		verify(passwordEncoder, never()).encode(anyString());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void testGetAuthenticatedUser_Success() {
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		User result = userService.getAuthenticatedUser();

		assertEquals(user, result);
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	void testGetAuthenticatedUser_UserNotFound() {
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getAuthenticatedUser());
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	void testUserProfileUpdateGet_Success() {
		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		UserProfileUpdateDto result = userService.userProfileUpdateGet();

		assertEquals(user.getFullName(), result.getFullName());
		assertEquals(user.getPassword(), result.getPassword());
		assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
		assertEquals(user.getProfilePicture(), result.getProfileImageUrl());
		verify(userRepository, times(1)).findByEmail(email);
	}

	@Test
	void testUpdateUserProfile_FullUpdate() {
		UserProfileUpdateDto dto = new UserProfileUpdateDto();
		dto.setFullName("Updated Name");
		dto.setPassword("newPassword");
		dto.setPhoneNumber("0987654321");
		dto.setProfileImageUrl("http://example.com/new.jpg");

		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		boolean result = userService.updateUserProfile(dto);

		assertTrue(result);
		assertEquals("Updated Name", user.getFullName());
		assertEquals("encodedNewPassword", user.getPassword());
		assertEquals("0987654321", user.getPhoneNumber());
		verify(userRepository, times(1)).findByEmail(email);
		verify(passwordEncoder, times(1)).encode("newPassword");
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testUpdateUserProfile_PartialUpdate_NoPassword() {
		UserProfileUpdateDto dto = new UserProfileUpdateDto();
		dto.setFullName("Updated Name");
		dto.setPhoneNumber("0987654321");

		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);

		boolean result = userService.updateUserProfile(dto);

		assertTrue(result);
		assertEquals("Updated Name", user.getFullName());
		assertEquals("encodedPassword", user.getPassword()); // Original password remains
		assertEquals("0987654321", user.getPhoneNumber());
		verify(userRepository, times(1)).findByEmail(email);
		verify(passwordEncoder, never()).encode(anyString());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testUpdateUserProfile_EmptyPassword() {
		UserProfileUpdateDto dto = new UserProfileUpdateDto();
		dto.setFullName("Updated Name");
		dto.setPassword(""); // Empty password
		dto.setPhoneNumber("0987654321");

		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);

		boolean result = userService.updateUserProfile(dto);

		assertTrue(result);
		assertEquals("Updated Name", user.getFullName());
		assertEquals("encodedPassword", user.getPassword()); // Password unchanged
		assertEquals("0987654321", user.getPhoneNumber());
		verify(userRepository, times(1)).findByEmail(email);
		verify(passwordEncoder, never()).encode(anyString());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testUpdateUserProfile_NullFields() {
		UserProfileUpdateDto dto = new UserProfileUpdateDto();
		dto.setFullName(null); // Null field
		dto.setPassword(null); // Null password
		dto.setPhoneNumber(null); // Null field

		authUtilMockedStatic.when(AuthUtil::getAuthenticatedUsername).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);

		boolean result = userService.updateUserProfile(dto);

		assertTrue(result);
		assertEquals("Test User", user.getFullName()); // Unchanged
		assertEquals("encodedPassword", user.getPassword()); // Unchanged
		assertEquals("1234567890", user.getPhoneNumber()); // Unchanged
		verify(userRepository, times(1)).findByEmail(email);
		verify(passwordEncoder, never()).encode(anyString());
		verify(userRepository, times(1)).save(user);
	}
}

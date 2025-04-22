package com.bytebreeze.quickdrop.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class AuthUtilTest {

	@Test
	void testGetAuthenticatedUsername_WithUserDetails() {
		// Arrange: Create mock objects
		SecurityContext mockContext = mock(SecurityContext.class);
		Authentication mockAuth = mock(Authentication.class);
		UserDetails mockUserDetails = mock(UserDetails.class);

		when(mockUserDetails.getUsername()).thenReturn("testuser");
		when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);
		when(mockContext.getAuthentication()).thenReturn(mockAuth);

		// Set SecurityContext in SecurityContextHolder
		SecurityContextHolder.setContext(mockContext);

		// Act
		String username = AuthUtil.getAuthenticatedUsername();

		// Assert
		assertEquals("testuser", username);
	}

	@Test
	void testGetAuthenticatedUsername_WithNonUserDetails() {
		// Arrange: Create mock objects
		SecurityContext mockContext = mock(SecurityContext.class);
		Authentication mockAuth = mock(Authentication.class);

		when(mockAuth.getPrincipal()).thenReturn("nonUser");
		when(mockContext.getAuthentication()).thenReturn(mockAuth);

		// Set SecurityContext in SecurityContextHolder
		SecurityContextHolder.setContext(mockContext);

		// Act
		String username = AuthUtil.getAuthenticatedUsername();

		// Assert
		assertEquals("nonUser", username);
	}

	@Test
	void testGetAuthenticatedUsername_WithNullAuthentication() {
		// Arrange: Create mock objects
		SecurityContext mockContext = mock(SecurityContext.class);
		when(mockContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(mockContext);

		// Act
		String username = AuthUtil.getAuthenticatedUsername();

		// Assert
		assertNull(username);
	}

	@Test
	void testIsAuthenticated_WithAuthenticatedUser() {
		// Arrange
		SecurityContext mockContext = mock(SecurityContext.class);
		Authentication mockAuth = mock(Authentication.class);
		UserDetails mockUserDetails = mock(UserDetails.class);

		when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);
		when(mockContext.getAuthentication()).thenReturn(mockAuth);
		SecurityContextHolder.setContext(mockContext);

		// Act
		boolean isAuthenticated = AuthUtil.isAuthenticated();

		// Assert
		assertTrue(isAuthenticated);
	}

	@Test
	void testIsAuthenticated_WithNullAuthentication() {
		// Arrange
		SecurityContext mockContext = mock(SecurityContext.class);
		when(mockContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(mockContext);

		// Act
		boolean isAuthenticated = AuthUtil.isAuthenticated();

		// Assert
		assertFalse(isAuthenticated);
	}

	@Test
	void testGetAuthenticatedUserRoles_WithNoRoles() {
		// Arrange
		SecurityContext mockContext = mock(SecurityContext.class);
		Authentication mockAuth = mock(Authentication.class);

		when(mockAuth.getAuthorities()).thenReturn(List.of());
		when(mockContext.getAuthentication()).thenReturn(mockAuth);
		SecurityContextHolder.setContext(mockContext);

		// Act
		List<String> roles = AuthUtil.getAuthenticatedUserRoles();

		// Assert
		assertTrue(roles.isEmpty());
	}

	@Test
	void testGetAuthenticatedUserRoles_WithNullAuthentication() {
		// Arrange
		SecurityContext mockContext = mock(SecurityContext.class);
		when(mockContext.getAuthentication()).thenReturn(null);
		SecurityContextHolder.setContext(mockContext);

		// Act
		List<String> roles = AuthUtil.getAuthenticatedUserRoles();

		// Assert
		assertTrue(roles.isEmpty());
	}
}

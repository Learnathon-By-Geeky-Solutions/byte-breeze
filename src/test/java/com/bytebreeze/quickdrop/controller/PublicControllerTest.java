package com.bytebreeze.quickdrop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.util.AuthUtil;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;

class PublicControllerTest {

	private PublicController publicController;
	private Model model;
	private MockedStatic<AuthUtil> authUtilMock;

	@BeforeEach
	void setUp() {
		publicController = new PublicController();
		model = mock(Model.class);
		authUtilMock = mockStatic(AuthUtil.class);
	}

	@AfterEach
	void tearDown() {
		authUtilMock.close();
	}

	@ParameterizedTest
	@CsvSource({
		"ROLE_ADMIN, redirect:/admin/dashboard",
		"ROLE_USER, redirect:/user/dashboard",
		"ROLE_RIDER, redirect:/rider/dashboard"
	})
	void home_authenticatedUser_redirectsBasedOnRole(String role, String expectedRedirect) {
		when(AuthUtil.isAuthenticated()).thenReturn(true);
		when(AuthUtil.getAuthenticatedUserRoles()).thenReturn(List.of(role));

		String viewName = publicController.home(model);
		assertEquals(expectedRedirect, viewName);
	}

	@Test
	void home_notAuthenticated_returnsIndexPage() {
		authUtilMock.when(AuthUtil::isAuthenticated).thenReturn(false);

		String viewName = publicController.home(model);

		verify(model).addAttribute("title", "Dashboard - Home");
		assertEquals("index", viewName);
	}

	@Test
	void home_authenticatedWithNoRoles_returnsIndexPage() {
		authUtilMock.when(AuthUtil::isAuthenticated).thenReturn(true);
		authUtilMock.when(AuthUtil::getAuthenticatedUserRoles).thenReturn(List.of());

		String viewName = publicController.home(model);

		verify(model).addAttribute("title", "Dashboard - Home");
		assertEquals("index", viewName);
	}
}

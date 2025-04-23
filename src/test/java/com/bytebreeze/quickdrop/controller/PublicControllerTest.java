package com.bytebreeze.quickdrop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.util.AuthUtil;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;

@ActiveProfiles("test")
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

	@Test
	void home_authenticatedAdmin_redirectsToAdminDashboard() {
		authUtilMock.when(AuthUtil::isAuthenticated).thenReturn(true);
		authUtilMock.when(AuthUtil::getAuthenticatedUserRoles).thenReturn(List.of("ROLE_ADMIN"));

		String viewName = publicController.home(model);

		assertEquals("redirect:/admin/dashboard", viewName);
	}

	@Test
	void home_authenticatedUser_redirectsToUserDashboard() {
		authUtilMock.when(AuthUtil::isAuthenticated).thenReturn(true);
		authUtilMock.when(AuthUtil::getAuthenticatedUserRoles).thenReturn(List.of("ROLE_USER"));

		String viewName = publicController.home(model);

		assertEquals("redirect:/user/dashboard", viewName);
	}

	@Test
	void home_authenticatedRider_redirectsToRiderDashboard() {
		authUtilMock.when(AuthUtil::isAuthenticated).thenReturn(true);
		authUtilMock.when(AuthUtil::getAuthenticatedUserRoles).thenReturn(List.of("ROLE_RIDER"));

		String viewName = publicController.home(model);

		assertEquals("redirect:/rider/dashboard", viewName);
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

package com.bytebreeze.quickdrop.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.dto.request.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.service.UserService;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class ProfileSettingUtilTest {

	@Test
	void testHandleUserProfileUpdate_HasErrors() {
		BindingResult bindingResult = mock(BindingResult.class);
		Model model = mock(Model.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		UserService userService = mock(UserService.class);
		UserProfileUpdateDto dto = new UserProfileUpdateDto();

		when(bindingResult.hasErrors()).thenReturn(true);

		String result = ProfileSettingUtil.handleUserProfileUpdate(
				dto, bindingResult, redirectAttributes, model, userService, "profile-page", "/success");

		assertEquals("profile-page", result);
	}

	@Test
	void testHandleUserProfileUpdate_Success() {
		BindingResult bindingResult = mock(BindingResult.class);
		Model model = mock(Model.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		UserService userService = mock(UserService.class);
		UserProfileUpdateDto dto = new UserProfileUpdateDto();

		when(bindingResult.hasErrors()).thenReturn(false);

		String result = ProfileSettingUtil.handleUserProfileUpdate(
				dto,
				bindingResult,
				redirectAttributes,
				model,
				userService,
				"profile-page",
				"/profile-settings?success");

		assertEquals("redirect:/profile-settings?success", result);
		verify(userService, times(1)).updateUserProfile(dto);
		verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Profile updated successfully!");
	}

	@Test
	void testHandleUserProfileUpdate_ExceptionThrown() {
		BindingResult bindingResult = mock(BindingResult.class);
		Model model = mock(Model.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		UserService userService = mock(UserService.class);
		UserProfileUpdateDto dto = new UserProfileUpdateDto();

		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException("update failed")).when(userService).updateUserProfile(dto);

		String result = ProfileSettingUtil.handleUserProfileUpdate(
				dto, bindingResult, redirectAttributes, model, userService, "profile-page", "/success");

		assertEquals("profile-page", result);
		verify(model, times(1)).addAttribute(eq("updateError"), anyString());
	}

	@Test
	void testPrivateConstructorCoverage() throws Exception {
		Constructor<ProfileSettingUtil> constructor = ProfileSettingUtil.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);

		assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
		assertEquals(
				"Utility class - instantiation not allowed", thrown.getCause().getMessage());
	}
}

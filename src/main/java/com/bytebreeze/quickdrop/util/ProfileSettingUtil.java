package com.bytebreeze.quickdrop.util;

import com.bytebreeze.quickdrop.dto.request.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.service.UserService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ProfileSettingUtil {

	private ProfileSettingUtil() {
		throw new UnsupportedOperationException("Utility class - instantiation not allowed");
	}

	public static String handleUserProfileUpdate(
			UserProfileUpdateDto dto,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model,
			UserService userService,
			String viewPage,
			String redirectUrl) {

		if (bindingResult.hasErrors()) return viewPage;

		try {
			userService.updateUserProfile(dto);
		} catch (Exception e) {
			model.addAttribute("updateError", "An error occurred while updating your profile. Please try again.");
			return viewPage;
		}
		redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
		return "redirect:" + redirectUrl;
	}
}

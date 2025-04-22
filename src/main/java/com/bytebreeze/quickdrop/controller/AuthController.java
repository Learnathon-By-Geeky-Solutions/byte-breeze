package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.request.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.service.UserService;
import com.bytebreeze.quickdrop.util.AuthUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	private static final String AUTH_REGISTER_VIEW = "auth/register";

	@GetMapping("/register")
	public String getRegistrationPage(Model model) {
		if (AuthUtil.isAuthenticated()) return "redirect:/user/dashboard";
		model.addAttribute("userRegistrationRequestDTO", new UserRegistrationRequestDTO());

		return AUTH_REGISTER_VIEW;
	}

	@PostMapping("/register")
	public String submitRegistrationForm(
			@Valid @ModelAttribute("userRegistrationRequestDTO") UserRegistrationRequestDTO userRegistrationRequestDTO,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("error");

			// Collect all validation error messages
			List<String> errorMessages = bindingResult.getAllErrors().stream()
					.map(error -> error.getDefaultMessage())
					.toList();

			// Add error messages to the model
			model.addAttribute("validationErrors", errorMessages);

			return AUTH_REGISTER_VIEW;
		}

		try {
			userService.registerUser(userRegistrationRequestDTO);

			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/auth/login";

		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return AUTH_REGISTER_VIEW;
		}
	}

	@GetMapping("/login")
	public String login(Model model) {
		if (AuthUtil.isAuthenticated()) return "redirect:/user/dashboard";
		return "auth/login";
	}

	@GetMapping("/forget-password")
	public String forgetPassword(Model model) {
		model.addAttribute("message", "Recover your account");
		return "auth/forget-password";
	}

	@GetMapping("/reset-password")
	public String resetPassword(Model model) {
		model.addAttribute("message", "Reset your password");
		return "auth/reset-password";
	}
}

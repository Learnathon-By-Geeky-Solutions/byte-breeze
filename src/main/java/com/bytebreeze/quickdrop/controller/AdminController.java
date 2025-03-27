package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.dto.response.RiderApprovalByAdminResponseDTO;
import com.bytebreeze.quickdrop.dto.response.RiderDetailsResponseDto;
import com.bytebreeze.quickdrop.enums.VerificationStatus;
import com.bytebreeze.quickdrop.service.RiderService;
import com.bytebreeze.quickdrop.service.UserService;
import com.bytebreeze.quickdrop.util.AuthUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private static final String DASHBOARD_PROFILE_SETTINGS_PAGE = "dashboard/admin-account";

	private final UserService userService;
	private final RiderService riderService;

	public AdminController(UserService userService, RiderService riderService) {
		this.userService = userService;
		this.riderService = riderService;
	}

	@GetMapping("/login")
	public String login() {
		if (AuthUtil.isAuthenticated()) return "redirect:/admin/dashboard";
		return "auth/admin-login";
	}

	@GetMapping("/dashboard")
	public String adminDashboard() {
		return "dashboard/admin-dashboard"; // Renders `admin-dashboard.html`
	}

	@GetMapping("/profile-settings")
	public String basicProfileSettings(Model model) {
		model.addAttribute("userProfileUpdateDto", userService.userProfileUpdateGet());
		return DASHBOARD_PROFILE_SETTINGS_PAGE;
	}

	@PostMapping("/profile-settings")
	public String updateAccountSettings(
			@Valid @ModelAttribute("userProfileUpdateDto") UserProfileUpdateDto dto,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (bindingResult.hasErrors()) {
			return DASHBOARD_PROFILE_SETTINGS_PAGE;
		}

		try {
			userService.updateUserProfile(dto);
		} catch (Exception e) {
			model.addAttribute("updateError", "An error occurred while updating your profile. Please try again.");
			return DASHBOARD_PROFILE_SETTINGS_PAGE;
		}

		redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
		return "redirect:/admin/profile-settings?success";
	}

	@GetMapping("/riders/pending")
	public String approvalPendingRiders(Model model) {

		List<RiderApprovalByAdminResponseDTO> pendingRiders = riderService.getPendingRiders();

		model.addAttribute("riders", pendingRiders);

		return "admin/pending-riders";
	}

	@PostMapping("/rider/approve")
	public String approveRider(@RequestParam("riderId") UUID riderId, RedirectAttributes redirectAttributes) {

		try {
			riderService.updateRiderVerificationStatus(riderId, VerificationStatus.VERIFIED);

			redirectAttributes.addFlashAttribute("message", "Rider approved successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to approve rider");
		}
		return "redirect:/admin/riders/pending";
	}

	@PostMapping("/rider/reject")
	public String rejectRider(@RequestParam("riderId") UUID riderId, RedirectAttributes redirectAttributes) {

		try {
			riderService.updateRiderVerificationStatus(riderId, VerificationStatus.REJECTED);

			redirectAttributes.addFlashAttribute("message", "Rider rejected successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to reject rider");
		}
		return "redirect:/admin/riders/pending";
	}

	@GetMapping("/rider/view/{id}")
	public String viewRiderDetails(@PathVariable("id") UUID riderId, Model model) {
		RiderDetailsResponseDto rider = riderService.getRiderDetails(riderId);
		model.addAttribute("rider", rider);
		return "admin/view-rider-details";
	}
}

package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.dto.response.RiderViewCurrentParcelsResponseDTO;
import com.bytebreeze.quickdrop.enums.ParcelStatus;
import com.bytebreeze.quickdrop.model.Parcel;
import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.service.RiderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

	private final RiderService riderService;

	@GetMapping("/login")
	public String riderLogin(Model model) {
		return "auth/rider-login";
	}

	@GetMapping("/dashboard")
	public String riderDashboard(Model model) {
		model.addAttribute("riderDashboardResponseDTO", riderService.riderDashboardResponse());

		return "dashboard/rider-dashboard";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("riderRegistrationRequestDTO", new RiderRegistrationRequestDTO());
		return "auth/rider-register";
	}

	@PostMapping("/register")
	public String submitRegistrationForm(
			@Valid @ModelAttribute("riderRegistrationRequestDTO")
					RiderRegistrationRequestDTO riderRegistrationRequestDTO,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("error");

			// Collect all validation error messages
			List<String> errorMessages = bindingResult.getAllErrors().stream()
					.map(error -> error.getDefaultMessage()) // Get default messages from dto
					.collect(Collectors.toList());

			// Add error messages to the model
			model.addAttribute("validationErrors", errorMessages);

			return "auth/rider-register";
		}

		try {
			Rider savedRider = riderService.registerRider(riderRegistrationRequestDTO);

			redirectAttributes.addFlashAttribute("success", "Rider Registration Successful"); // Add flash attribute
			return "redirect:/rider/onboarding/" + savedRider.getId();

		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "auth/rider-register";
		}
	}

	@GetMapping("/onboarding/{riderId}")
	public String showRiderOnboardingForm(@PathVariable UUID riderId, Model model) {

		try {
			Rider rider = riderService.findByRiderId(riderId);

			// sending rider to store rider ID
			model.addAttribute("rider", rider);

			// Check that previous any page redirect the DTO or Not, if not, then add new dto.
			if (!model.containsAttribute("riderOnboardingDTO")) {
				model.addAttribute("riderOnboardingDTO", new RiderOnboardingDTO()); // Initialize if missing
			}
			// model.addAttribute("riderOnboardingDTO", new RiderOnboardingDTO());
			return "rider/onboarding";

		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "auth/rider-register";
		}
	}

	@PostMapping("/onboarding")
	public String submitRiderOnboardingForm(
			@RequestParam("riderId") UUID riderId,
			@Valid @ModelAttribute("riderOnboardingDTO") RiderOnboardingDTO riderOnboardingDTO,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("error");

			// Collect all validation error messages
			List<String> errorMessages = bindingResult.getAllErrors().stream()
					.map(error -> error.getDefaultMessage()) // Get default messages from dto
					.collect(Collectors.toList());

			// Add error messages to the model
			redirectAttributes.addFlashAttribute("validationErrors", errorMessages);

			// Return the DTO as FlashAttribute to next redirect get req. to retain the previous entered
			// data in field.
			redirectAttributes.addFlashAttribute("riderOnboardingDTO", riderOnboardingDTO);

			return "redirect:/rider/onboarding/" + riderId;
		}
		try {
			riderService.onboardRider(riderId, riderOnboardingDTO);

			redirectAttributes.addFlashAttribute("success", "Rider Onboarded successfully. Verification Pending.");

			return "redirect:/rider/login";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Onboarding failed: " + e.getMessage());

			// Return the DTO as FlashAttribute to next redirect get req. to retain the previous entered
			// data in field.
			redirectAttributes.addFlashAttribute("riderOnboardingDTO", riderOnboardingDTO);

			return "redirect:/rider/onboarding/" + riderId;
		}
	}

	@PostMapping("/status")
	public String updateStatus(@RequestParam Boolean status) {
		riderService.updateRiderStatus(status);
		return "redirect:/rider/dashboard";
	}

	@GetMapping("/current-parcels")
	public String showCurrentParcelsRequest(Model model) {

		Rider rider = riderService.getAuthenticatedRider();
		if (rider.getIsAssigned()) {

			try {

				List<Parcel> parcels = riderService.getAssignedParcelByRider(rider);
				if (parcels != null) {
					// log.info("Get assigned parcel by rider: {}", parcels.getCategory());
					model.addAttribute("parcels", parcels);
				} else {
					model.addAttribute("error", "No parcel assigned despite rider being marked as assigned");
				}

			} catch (Exception e) {
				model.addAttribute("error", "Failed to load parcel : " + e.getMessage());
			}
			return "rider/view-assigned-parcels";
		} else {

			List<RiderViewCurrentParcelsResponseDTO> currentParcels = riderService.CurrentParcelsForRider();
			model.addAttribute("parcels", currentParcels);

			return "rider/view-current-parcels";
		}
	}

	@PostMapping("/Accept/{parcelId}")
	public String acceptParcel(@PathVariable UUID parcelId, RedirectAttributes redirectAttributes) {

		try {
			riderService.acceptParcelDelivery(parcelId);
			redirectAttributes.addFlashAttribute("success", "Parcel accepted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to accept parcel: " + e.getMessage());
		}

		return "redirect:/rider/current-parcels";
	}

	@PostMapping ("parcels/{id}/update-status")
	public String updateAssignedParcelStatus(@PathVariable UUID id,
											 @RequestParam ParcelStatus status,
											 RedirectAttributes redirectAttributes,
											 Model model) {

		try {
			//parcelService.updateParcelStatus(id, status);
			redirectAttributes.addFlashAttribute("success", "Parcel status updated successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to update status.");
		}

		return "redirect:/rider/current-parcels";

	}


}

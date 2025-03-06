package com.bytebreeze.quickdrop.controller;


import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.RiderRegistrationRequestDTO;
import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public String adminDashboard() {
        return "dashboard/rider-dashboard";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("riderRegistrationRequestDTO", new RiderRegistrationRequestDTO());
        return "auth/rider-register";
    }

    @PostMapping("/register")
    public String submitRegistrationForm(@Valid @ModelAttribute("riderRegistrationRequestDTO") RiderRegistrationRequestDTO riderRegistrationRequestDTO,
                                         BindingResult bindingResult,
                                         Model model,
                                         RedirectAttributes redirectAttributes
    ) {
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
            return "rider-register";
        }
    }


    @GetMapping("/onboarding/{riderId}")
    public String showRiderOnboardingForm(@PathVariable UUID riderId, Model model) {

        try {
            Rider rider = riderService.findByRiderId(riderId);
            model.addAttribute("rider", rider);
            model.addAttribute("riderOnboardingDTO", new RiderOnboardingDTO());
            return "rider/onboarding";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "rider-register";
        }

    }


    @PostMapping("/onboarding")
    public String submitRiderOnboardingForm(@RequestParam("riderId") UUID riderId,
                                            @Valid @ModelAttribute("riderOnboardingDTO") RiderOnboardingDTO riderOnboardingDTO,
                                            BindingResult bindingResult,
                                            Model model,
                                            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error");

            // Collect all validation error messages
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()) // Get default messages from dto
                    .collect(Collectors.toList());

            // Add error messages to the model
            redirectAttributes.addFlashAttribute("validationErrors", errorMessages);

            return "redirect:/rider/onboarding/" + riderId;
        }
        try {
            riderService.onboardRider(riderId, riderOnboardingDTO);


            redirectAttributes.addFlashAttribute("success", "Rider Onboarded successfully. Verification Pending.");
            //return "redirect:/riders/success";
            return "redirect:/rider/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Onboarding failed: " + e.getMessage());
            return "redirect:/rider/onboarding/" + riderId;
        }

    }

}

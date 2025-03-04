package com.bytebreeze.quickdrop.controller;


import com.bytebreeze.quickdrop.dto.RiderOnboardingDTO;
import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import com.bytebreeze.quickdrop.model.Rider;
import com.bytebreeze.quickdrop.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @GetMapping("/onboarding")
    public String showRiderOnboardingForm(Model model){

        model.addAttribute("riderOnboardingDTO",new RiderOnboardingDTO());

        return "rider/onboarding";
    }
    @PostMapping("/onboarding")
    public String submitRiderOnboardingForm(@Valid @ModelAttribute("riderOnboardingDTO") RiderOnboardingDTO riderOnboardingDTO,
                                            BindingResult bindingResult,
                                            Model model,
                                            RedirectAttributes redirectAttributes
    ){

        if(bindingResult.hasErrors()){
            model.addAttribute("error");

            // Collect all validation error messages
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()) // Get default messages from dto
                    .collect(Collectors.toList());

            // Add error messages to the model
            model.addAttribute("validationErrors", errorMessages);

            return "rider/onboarding";
        }
        try {
            riderService.onboardRider(riderOnboardingDTO);


            redirectAttributes.addFlashAttribute("success", "Rider Onboarded successfully. Verification Pending.");
            //return "redirect:/riders/success";
            return "redirect:/user/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Onboarding failed: " + e.getMessage());
            return "redirect:/user/dashboard";
        }

    }

}

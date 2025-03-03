package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.model.Parcel;
import com.bytebreeze.quickdrop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.bytebreeze.quickdrop.repository.ParcelRepository;
import com.bytebreeze.quickdrop.service.ParcelService;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private static final String DASHBOARD_PROFILE_SETTINGS_PAGE = "dashboard/account";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String userDashboard() {
        return "dashboard/home";
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
        return "redirect:/user/profile-settings?success";
    }

    //  Show the Parcel Tracking Form
    @Autowired
    private ParcelRepository parcelRepository;

    @GetMapping("/parcel-tracking")
    public String showParcelForm(Model model) {
        // Check if there is a success message to display
        if (model.containsAttribute("message")) {
            model.addAttribute("message", model.getAttribute("message"));
        }
        return "dashboard/parcel-tracking"; // Ensure this path matches the location of your Thymeleaf template
    }

    //  Handle the Parcel Form Submission
    @PostMapping("/parcel-tracking")
    public String createParcel(
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam double weight,
            @RequestParam String size,
            @RequestParam String pickup_location,
            @RequestParam String receiver_name,
            @RequestParam String receiver_address,
            Model model) {

        Parcel parcel = new Parcel(category, description, weight, size, pickup_location, receiver_name, receiver_address);
        parcelRepository.save(parcel);

        // Add success message to be displayed on the form page
        model.addAttribute("message", "Parcel Saved Successfully!");

        return "redirect:/user/parcel-tracking-list"; // Stay on the same page with the success message
    }

    // 📌 Show the Parcel Tracking Data
    @Autowired
    private ParcelService parcelService;

    @GetMapping("/parcel-tracking-list")
    public String getAllParcels(Model model) {
        // Fetch all parcels from the service
        List<Parcel> parcels = parcelService.getAllParcels();

        // Add parcels to the model to pass them to the view
        model.addAttribute("parcels", parcels);
        return "dashboard/parcel-tracking-list"; // The template to display the list
    }
}

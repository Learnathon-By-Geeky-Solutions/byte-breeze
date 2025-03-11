package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.dto.request.ParcelBookingRequestDTO;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import com.bytebreeze.quickdrop.service.ParcelService;
import com.bytebreeze.quickdrop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private static final String DASHBOARD_PROFILE_SETTINGS_PAGE = "dashboard/account";
    private final ProductCategoryRepository productCategoryRepository;
    private final ParcelService parcelService;

    public UserController(UserService userService, ProductCategoryRepository productCategoryRepository, ParcelService parcelService) {
        this.userService = userService;
        this.productCategoryRepository = productCategoryRepository;
        this.parcelService = parcelService;
    }

    @GetMapping("/dashboard")
    public String userDashboard() {
        return "dashboard/home";
    }

    @GetMapping("/profile-settings")
    public String basicProfileSettings(Model model)
    {
        model.addAttribute("userProfileUpdateDto", userService.userProfileUpdateGet());
        return DASHBOARD_PROFILE_SETTINGS_PAGE;
    }

    @PostMapping("/profile-settings")
    public String updateAccountSettings(
            @Valid @ModelAttribute("userProfileUpdateDto") UserProfileUpdateDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model)
    {

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

    @GetMapping("/book-parcel")
    public String bookParcel(Model model) {
        model.addAttribute("productCategories", productCategoryRepository.findAll());
        model.addAttribute("title", "Book Parcel");
        model.addAttribute("parcelBookingRequestDTO", new ParcelBookingRequestDTO());
        return "dashboard/book-parcel";
    }

    @PostMapping("/book-parcel")
    public String handleParcelBooking(@ModelAttribute @Valid ParcelBookingRequestDTO parcelBookingRequestDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productCategories", productCategoryRepository.findAll());
            model.addAttribute("title", "Book Parcel");
            return "dashboard/book-parcel";
        }

        parcelService.bookParcel(parcelBookingRequestDTO);

        return "redirect:/user/dashboard?success";
    }
}

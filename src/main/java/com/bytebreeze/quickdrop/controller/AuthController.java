package com.bytebreeze.quickdrop.controller;


import com.bytebreeze.quickdrop.dto.UserRegistrationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/register")
    public String getRegistrationPage(Model model) {
        model.addAttribute("userRegistrationRequestDTO",new UserRegistrationRequestDTO());

        return "auth/register";
    }

    @PostMapping ("/register")
    public String submitRegistrationForm(@Valid @ModelAttribute("userRegistrationRequestDTO") UserRegistrationRequestDTO userRegistrationRequestDTO,
                                         BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return "auth/register";
        }
        System.out.println("Form Data name: "+ userRegistrationRequestDTO.getFullName());
        System.out.println("Form Data email: "+ userRegistrationRequestDTO.getEmail());
        System.out.println("Form Data password: "+ userRegistrationRequestDTO.getPassword());

        return "redirect:/auth/login";
    }







    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", "Login to your account");
        return "auth/login";  // Removed leading slash
    }


    @GetMapping("/forget-password")
    public String forgetPassword(Model model) {
        model.addAttribute("message", "Recover your account");
        return "auth/forget-password";  // Removed leading slash
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("message", "Reset your password");
        return "auth/reset-password";  // Removed leading slash
    }



}

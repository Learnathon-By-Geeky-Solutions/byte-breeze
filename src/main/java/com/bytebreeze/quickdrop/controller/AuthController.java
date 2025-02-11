package com.bytebreeze.quickdrop.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/auth")
public class AuthController {



    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", "Login to your account");
        return "auth/login";  // Removed leading slash
    }

     @GetMapping("/register")
     public String register(Model model) {
         model.addAttribute("message", "Create a new account");
         return "auth/register";  // Removed leading slash
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

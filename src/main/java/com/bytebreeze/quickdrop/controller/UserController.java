package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.dto.UserProfileUpdateDto;
import com.bytebreeze.quickdrop.model.User;
import com.bytebreeze.quickdrop.repository.UserRepository;
import com.bytebreeze.quickdrop.service.UserService;
import com.bytebreeze.quickdrop.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String userDashboard() {
        return "dashboard/home";
    }

    @GetMapping("/profile-settings")
    public String basicProfileSettings(Model model)
    {
        model.addAttribute("userProfileUpdateDto", userService.userProfileUpdateGet());
        return "dashboard/account";
    }
}

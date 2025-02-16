package com.bytebreeze.quickdrop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/login")
    public String adminLogin() {
        return "auth/admin-login"; // Renders `admin-login.html`
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "dashboard/admin-dashboard"; // Renders `admin-dashboard.html`
    }
}

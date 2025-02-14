package com.bytebreeze.quickdrop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "auth/admin-login"; // Renders `admin-login.html`
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "dashboard/admin-dashboard"; // Renders `admin-dashboard.html`
    }
}

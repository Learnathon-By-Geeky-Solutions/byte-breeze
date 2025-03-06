package com.bytebreeze.quickdrop.controller;

import com.bytebreeze.quickdrop.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping
public class PublicController {

    @GetMapping("/")
    public String home(Model model) {
        if(AuthUtil.isAuthenticated())
        {
            List<String> userRoles  = AuthUtil.getAuthenticatedUserRoles();
            if(userRoles.contains("ROLE_ADMIN")) return "redirect:/admin/dashboard";
            if(userRoles.contains("ROLE_USER")) return "redirect:/user/dashboard";
            if(userRoles.contains("ROLE_RIDER")) return "redirect:/rider/dashboard";
        }
        model.addAttribute("title", "Dashboard - Home");
        return "index";
    }


}

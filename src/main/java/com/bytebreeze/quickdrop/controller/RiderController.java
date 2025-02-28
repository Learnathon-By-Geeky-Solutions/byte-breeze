package com.bytebreeze.quickdrop.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rider")
public class RiderController {

    @GetMapping("/onboarding")
    public String showRiderOnboardingForm(){


        return "rider/onboarding";
    }

}

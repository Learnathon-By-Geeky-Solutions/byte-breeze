package com.bytebreeze.quickdrop.controller;

import java.util.Arrays;
import java.util.List;

import com.bytebreeze.quickdrop.model.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping
public class Dashboard {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Dashboard - Home");
        return "index";  // Removed leading slash
    }

    @GetMapping("/forms")
    public String formPages(Model model) {
        model.addAttribute("title", "Dashboard - Form Pages");
        return "dashboard/forms";  // Removed leading slash
    }

    @GetMapping("/tables")
    public String tablePages(Model model) {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "Dakota Rice", "$36,738", "United States", "Oud-Turnhout"),
                new Employee(2, "Minerva Hooper", "$23,789", "Curaçao", "Sinaai-Waas"),
                new Employee(3, "Sage Rodriguez", "$56,142", "Netherlands", "Baileux"),
                new Employee(4, "Philip Chaney", "$38,735", "Korea, South", "Overland Park"),
                new Employee(5, "Doris Greene", "$63,542", "Malawi", "Feldkirchen in Kärnten"));

        model.addAttribute("employees", employees);
        model.addAttribute("title", "Dashboard - Table Pages");
        return "dashboard/tables";  // Removed leading slash
    }

    @GetMapping("/account-settings")
    public String accountSettings(Model model) {
        model.addAttribute("title", "Dashboard - Account Settings");
        return "dashboard/account";  // Removed leading slash
    }
}

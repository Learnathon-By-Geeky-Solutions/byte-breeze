package com.bytebreeze.quickdrop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/parcel")
public class ParcelController {

    @GetMapping("/book")
    public String bookParcel() {
        return "dashboard/book-parcel";
    }
}

package com.example.capstone_4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class PageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/register.html";
    }
}


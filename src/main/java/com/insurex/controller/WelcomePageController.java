package com.insurex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomePageController {

    @GetMapping("/")
    public String welcomePage() {
        return "welcomePage";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/coverages")
    public String coveragePage() {
        return "coverages";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contactPage";
    }
}
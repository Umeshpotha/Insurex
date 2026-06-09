package com.insurex.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomePageController {

    @GetMapping("/")
    public String welcomePage() {
        // 1. Grab the active security authorization session context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Check if the user is logged in (and make sure they aren't an anonymous guest)
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // ✅ Automatically forward them straight to the dashboard page
            boolean admin = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            return admin ? "redirect:/admin" : "redirect:/userDash";
        }

        // 3. Fallback for non-authenticated guests to access the landing home view
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

    @GetMapping("/wecomePage")
    public String toWelcomePage() {
        return "welcomePage";
    }
}

package com.insurex.controller;

import com.insurex.model.User;
import com.insurex.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        // 1. Intercept signed-in users trying to access the signup form
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/userDash";
        }

        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            service.register(user);
            return "redirect:/signin?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "signup";
        }
    }

    @GetMapping("/signin")
    public String signin() {
        // 2. Intercept active user sessions attempting to visit the sign-in page
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅ If logged in, automatically bounce them straight back to their dashboard
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/userDash";
        }

        return "signin";
    }

    @GetMapping("/userDash")
    public String dashboard() {
        return "userDash";
    }
}
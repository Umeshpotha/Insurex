package com.insurex.controller;

import com.insurex.model.User;
import com.insurex.model.Policy;
import com.insurex.model.Claim;
import com.insurex.repository.PolicyRepository;
import com.insurex.repository.ClaimRepository;
import com.insurex.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private final UserService service;
    // 💡 Added missing repository fields
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    // 💡 Injected repositories via the constructor (Spring handles this automatically)
    public AuthController(UserService service, PolicyRepository policyRepository, ClaimRepository claimRepository) {
        this.service = service;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
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

    // This handles the exact URL endpoint defined in your SecurityConfig success path
    @GetMapping("/userDash")
    public String dashboard(Model model) {
        // 1. Extract security login context email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentLoggedEmail = auth.getName();

        // 🔍 DEBUG PRINTS: Keep an eye on your IDE console terminal when you log in!
        System.out.println("=============================================");
        System.out.println("DEBUG -> LOGGED IN USER IS: " + currentLoggedEmail);

        // 2. Fetch all rows out of your DB tables (Now works perfectly!)
        List<Policy> availablePolicies = policyRepository.findAll();
        System.out.println("DEBUG -> POLICIES FOUND IN DB: " + availablePolicies.size());
        System.out.println("=============================================");

        List<Claim> userClaims = claimRepository.findByUser_Email(currentLoggedEmail);

        // 3. Bind everything to the UI context
        model.addAttribute("availablePolicies", availablePolicies);
        model.addAttribute("myClaims", userClaims);
        model.addAttribute("sessionEmail", currentLoggedEmail);

        // 4. Serves userDash.html cleanly
        return "userDash";
    }
}
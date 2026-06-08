package com.insurex.controller;

import com.insurex.model.User;
import com.insurex.model.Policy;
import com.insurex.model.Claim;
import com.insurex.repository.PolicyRepository;
import com.insurex.repository.ClaimRepository;
import com.insurex.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private final UserService service;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    // Combined Constructor injection
    public AuthController(UserService service, PolicyRepository policyRepository, ClaimRepository claimRepository) {
        this.service = service;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        service.register(user);
        return "redirect:/signin";
    }

    @GetMapping("/signin")
    public String signin() {
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

        // 2. Fetch all rows out of your DB tables
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
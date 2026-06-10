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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            return redirectFor(auth);
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
            return redirectFor(auth);
        }

        return "signin";
    }

    private String redirectFor(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/admin" : "redirect:/dashboard";
    }

    @PostMapping("/applyPolicy")
    public String applyPolicy(@RequestParam Long policyId, RedirectAttributes redirectAttributes) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = service.findByEmail(email);
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        if (!"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
            throw new IllegalArgumentException("This policy is not accepting applications");
        }

        if (claimRepository.existsByUser_EmailAndPolicyName(email, policy.getName())) {
            redirectAttributes.addFlashAttribute("error", "You already applied for this policy");
            return "redirect:/my-coverage";
        }

        Claim application = new Claim();
        application.setUser(user);
        application.setPolicyName(policy.getName());
        application.setCategory(policy.getCategory());
        application.setClaimAmount(policy.getCoverageAmount());
        application.setStatus("Pending");
        claimRepository.save(application);
        redirectAttributes.addFlashAttribute("success", "Policy application submitted");
        return "redirect:/my-coverage";
    }
}

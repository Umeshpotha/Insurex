package com.insurex.controller;

import com.insurex.config.DataInitializer;
import com.insurex.model.Claim;
import com.insurex.model.Policy;
import com.insurex.model.Role;
import com.insurex.repository.ClaimRepository;
import com.insurex.repository.PolicyRepository;
import com.insurex.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;

    public AdminController(PolicyRepository policyRepository, ClaimRepository claimRepository,
                           UserRepository userRepository) {
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        var policies = policyRepository.findAll();
        var claims = claimRepository.findAll();
        model.addAttribute("userName", authentication.getName());
        model.addAttribute("totalPolicies", policies.size());
        model.addAttribute("activePolicies", policies.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count());
        model.addAttribute("pendingClaims", claims.stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("monthlyPremiums", policies.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).mapToDouble(Policy::getPremium).sum());
        model.addAttribute("activeUsers", userRepository.findByRoleOrderByUsernameAsc(Role.USER).size());
        model.addAttribute("processingRequests", claimRepository.countByStatusIgnoreCase("Pending"));
        model.addAttribute("recentPolicies", policies.stream().limit(5).toList());
        model.addAttribute("recentClaims", claims.stream().limit(5).toList());
        return "adminHome";
    }

    @GetMapping("/policies")
    public String policies(Model model) {
        model.addAttribute("policy", new Policy());
        model.addAttribute("policies", policyRepository.findAll());
        return "policies";
    }

    @PostMapping("/policies")
    public String createPolicy(@Valid @ModelAttribute Policy policy, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors() || policy.getPremium() < 0 || policy.getCoverageAmount() < 0) {
            redirectAttributes.addFlashAttribute("error", "Enter valid policy details");
            return "redirect:/admin/policies";
        }
        policy.setId(null);
        policy.setStatus(policy.getStatus().toUpperCase());
        policyRepository.save(policy);
        redirectAttributes.addFlashAttribute("success", "Policy preset created");
        return "redirect:/admin/policies";
    }

    @PostMapping("/policies/{id}/update")
    public String updatePolicy(@PathVariable Long id, @ModelAttribute Policy input,
                               RedirectAttributes redirectAttributes) {
        if (input.getName() == null || input.getName().isBlank() || input.getCategory() == null
                || input.getPremium() == null || input.getPremium() < 0
                || input.getCoverageAmount() == null || input.getCoverageAmount() < 0
                || input.getStatus() == null) {
            redirectAttributes.addFlashAttribute("error", "Enter valid policy details");
            return "redirect:/admin/policies";
        }
        Policy policy = policyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        policy.setName(input.getName());
        policy.setCategory(input.getCategory());
        policy.setPremium(input.getPremium());
        policy.setCoverageAmount(input.getCoverageAmount());
        policy.setDescription(input.getDescription());
        policy.setStatus(input.getStatus().toUpperCase());
        policyRepository.save(policy);
        redirectAttributes.addFlashAttribute("success", "Policy updated");
        return "redirect:/admin/policies";
    }

    @PostMapping("/policies/{id}/delete")
    public String deletePolicy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        policyRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Policy deleted");
        return "redirect:/admin/policies";
    }

    @PostMapping("/policies/seed")
    public String seedPolicies(RedirectAttributes redirectAttributes) {
        policyRepository.saveAll(DataInitializer.defaultPolicies());
        redirectAttributes.addFlashAttribute("success", "Default policy presets added");
        return "redirect:/admin/policies";
    }

    @GetMapping("/claims")
    public String claims(Model model) {
        model.addAttribute("claims", claimRepository.findAll());
        return "claims";
    }

    @PostMapping("/claims/{id}/status")
    public String updateClaim(@PathVariable Long id, @RequestParam String status,
                              RedirectAttributes redirectAttributes) {
        if (!status.equalsIgnoreCase("Approved") && !status.equalsIgnoreCase("Rejected") && !status.equalsIgnoreCase("Pending")) {
            throw new IllegalArgumentException("Invalid claim status");
        }
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        claim.setStatus(status);
        claimRepository.save(claim);
        redirectAttributes.addFlashAttribute("success", "Application status updated");
        return "redirect:/admin/claims";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", userRepository.findByRoleOrderByUsernameAsc(Role.USER));
        return "customers";
    }
}

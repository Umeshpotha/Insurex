package com.insurex.controller;

import com.insurex.model.Claim;
import com.insurex.model.Policy;
import com.insurex.repository.ClaimRepository;
import com.insurex.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClaimRepository claimRepository;

    // Helper method to abstract shared Security Context email verification
    private String getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "client@insurex.com";
    }

    // 1. Overview Page Component (New Insurance Marketplace Catalog)
    @GetMapping("/dashboard")
    public String showUserDashboard(Model model) {
        String currentLoggedEmail = getAuthenticatedUserEmail();

        List<Policy> structuralNewPolicies = policyRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("ACTIVE");
        List<Claim> structuralUserClaims = claimRepository.findByUser_Email(currentLoggedEmail);

        model.addAttribute("availablePolicies", structuralNewPolicies);
        model.addAttribute("claimsCount", structuralUserClaims != null ? structuralUserClaims.size() : 0);
        model.addAttribute("sessionEmail", currentLoggedEmail);

        return "userDash"; // Maps to templates/userDash.html
    }

    // 2. My Coverages Tab (Enrolled and claimed policies tracker table)
    @GetMapping("/my-coverage")
    public String showMyCoverage(Model model) {
        String currentLoggedEmail = getAuthenticatedUserEmail();

        List<Policy> structuralNewPolicies = policyRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("ACTIVE");
        List<Claim> structuralUserClaims = claimRepository.findByUser_Email(currentLoggedEmail);

        model.addAttribute("availableCount", structuralNewPolicies != null ? structuralNewPolicies.size() : 0);
        model.addAttribute("myClaims", structuralUserClaims);
        model.addAttribute("sessionEmail", currentLoggedEmail);

        return "myCoverage"; // Maps to templates/myCoverage.html
    }

    // 3. Billing Invoices Ledger (Financial statements for active protections)
    @GetMapping("/billing")
    public String showBillingDetails(Model model) {
        String currentLoggedEmail = getAuthenticatedUserEmail();

        List<Policy> structuralNewPolicies = policyRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("ACTIVE");
        List<Claim> structuralUserClaims = claimRepository.findByUser_Email(currentLoggedEmail);

        model.addAttribute("availableCount", structuralNewPolicies != null ? structuralNewPolicies.size() : 0);
        // Uses the claim metrics list directly to capture transactional value properties
        model.addAttribute("billingRecords", structuralUserClaims);
        model.addAttribute("sessionEmail", currentLoggedEmail);

        return "billingDetails"; // Maps to templates/billingDetails.html
    }
}

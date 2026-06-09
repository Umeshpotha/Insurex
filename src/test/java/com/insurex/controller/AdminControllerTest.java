package com.insurex.controller;

import com.insurex.model.Claim;
import com.insurex.model.Policy;
import com.insurex.repository.ClaimRepository;
import com.insurex.repository.PolicyRepository;
import com.insurex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    private PolicyRepository policyRepository;
    private ClaimRepository claimRepository;
    private AdminController controller;

    @BeforeEach
    void setUp() {
        policyRepository = mock(PolicyRepository.class);
        claimRepository = mock(ClaimRepository.class);
        controller = new AdminController(policyRepository, claimRepository, mock(UserRepository.class));
    }

    @Test
    void createsValidPolicyPreset() {
        Policy policy = new Policy();
        policy.setName("Health Shield");
        policy.setCategory("Health");
        policy.setPremium(500.0);
        policy.setCoverageAmount(100000.0);
        policy.setStatus("active");
        var errors = new BeanPropertyBindingResult(policy, "policy");

        String view = controller.createPolicy(policy, errors, new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/admin/policies");
        assertThat(policy.getStatus()).isEqualTo("ACTIVE");
        verify(policyRepository).save(policy);
    }

    @Test
    void rejectsNegativePolicyValues() {
        Policy policy = new Policy();
        policy.setName("Invalid");
        policy.setCategory("Health");
        policy.setPremium(-1.0);
        policy.setCoverageAmount(1000.0);
        policy.setStatus("ACTIVE");

        controller.createPolicy(policy, new BeanPropertyBindingResult(policy, "policy"),
                new RedirectAttributesModelMap());

        verifyNoInteractions(policyRepository);
    }

    @Test
    void approvesPendingApplication() {
        Claim claim = new Claim();
        claim.setStatus("Pending");
        when(claimRepository.findById(7L)).thenReturn(Optional.of(claim));

        String view = controller.updateClaim(7L, "Approved", new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/admin/claims");
        assertThat(claim.getStatus()).isEqualTo("Approved");
        verify(claimRepository).save(claim);
    }
}

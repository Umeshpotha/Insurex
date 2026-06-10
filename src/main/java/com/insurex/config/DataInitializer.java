package com.insurex.config;

import com.insurex.model.Policy;
import com.insurex.model.Role;
import com.insurex.model.User;
import com.insurex.repository.PolicyRepository;
import com.insurex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(UserRepository userRepository, PolicyRepository policyRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${insurex.admin.email:admin@insurex.com}") String adminEmail,
                           @Value("${insurex.admin.password:admin123}") String adminPassword) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        createAdminIfMissing();
        if (policyRepository.count() == 0) {
            policyRepository.saveAll(defaultPolicies());
        } else {
            var policies = policyRepository.findAll();
            policies.forEach(policy -> {
                if (policy.getStatus() == null || policy.getStatus().isBlank()) {
                    policy.setStatus("ACTIVE");
                }
            });
            policyRepository.saveAll(policies);
        }
    }

    private void createAdminIfMissing() {
        if (userRepository.existsByEmail(adminEmail.toLowerCase())) {
            return;
        }
        User admin = new User();
        admin.setUsername("InsureX Administrator");
        admin.setEmail(adminEmail.toLowerCase());
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    public static List<Policy> defaultPolicies() {
        return List.of(
                policy("Family Health Shield", "Health", 1299.0, 500000.0, "Comprehensive family medical protection."),
                policy("Secure Life Plus", "Life", 899.0, 2000000.0, "Long-term life cover for financial security."),
                policy("Global Travel Guard", "Travel", 499.0, 300000.0, "Medical and trip protection for international travel.")
        );
    }

    private static Policy policy(String name, String category, double premium, double coverage, String description) {
        Policy policy = new Policy();
        policy.setName(name);
        policy.setCategory(category);
        policy.setPremium(premium);
        policy.setCoverageAmount(coverage);
        policy.setDescription(description);
        policy.setStatus("ACTIVE");
        return policy;
    }
}

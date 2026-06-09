package com.insurex.service;

import com.insurex.model.Role;
import com.insurex.model.User;
import com.insurex.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;

    // ✅ Removed PasswordEncoder dependency completely
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(User user) {
        user.setEmail(user.getEmail().trim().toLowerCase());

        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setRole(Role.USER);

        // ✅ Directly saving the plain-text password from the user input
        user.setPassword(user.getPassword());

        return repo.save(user);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

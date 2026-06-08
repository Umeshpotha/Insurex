package com.insurex.model;


import jakarta.persistence.*;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links directly to your existing User model's unique identity record
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false)
    private String category; // life, health, travel

    @Column(nullable = false)
    private Double claimAmount;

    @Column(nullable = false)
    private String status; // Pending, Approved, Rejected

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getClaimAmount() { return claimAmount; }
    public void setClaimAmount(Double claimAmount) { this.claimAmount = claimAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

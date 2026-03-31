package com.group13.ecopark_bicycle_parking.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "national_id", nullable = false, unique = true)
    private String nationalId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String address;

    private String role; // Admin, Manager, Customer

    @Column(name = "wallet_balance")
    private Long walletBalance = 0L;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist 
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null || role.trim().isEmpty()) {
            role = "Customer";
        }
        if (walletBalance == null) {
            walletBalance = 0L;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String full_name) {
        this.fullName = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String national_id) {
        this.nationalId = national_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Long wallet_balance) {
        this.walletBalance = wallet_balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.createdAt = created_at;
    }
}
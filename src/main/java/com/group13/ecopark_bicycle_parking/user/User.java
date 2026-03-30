package com.group13.ecopark_bicycle_parking.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "users")
public class User {
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long user_id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String full_name;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String national_id;
    
    @Column(nullable = false)
    private String phone_number;
    
    private String role; //Vai trò: Admin, Manager hoặc Customer
    
    private Long wallet_balance = 0L; 

    private LocalDateTime created_at; 

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
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

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNational_id() {
		return national_id;
	}

	public void setNational_id(String national_id) {
		this.national_id = national_id;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getWallet_balance() {
		return wallet_balance;
	}

	public void setWallet_balance(Long wallet_balance) {
		this.wallet_balance = wallet_balance;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
}

package com.group13.ecopark_bicycle_parking;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "wallet_transactions")
//Dành cho dữ liệu thanh đoán mua điểm
public class WalletTransaction {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long transaction_id;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id")
    private User user; 
    
    private Double amount; //Số tiền dương nếu là nạp, âm là hoàn tiền
    
    private String transaction_type; //Trạng thái: "Chuyển khoản, Tiền mặt, Hoàn tiền"
    
    private LocalDateTime created_at;

	public Long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
    
}

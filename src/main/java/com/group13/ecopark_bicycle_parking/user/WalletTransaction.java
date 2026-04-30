package com.group13.ecopark_bicycle_parking.user;

import com.group13.ecopark_bicycle_parking.rental.Rental;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WalletTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer transactionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rental_id")
	private Rental rental;

	private String transactionType;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;
}
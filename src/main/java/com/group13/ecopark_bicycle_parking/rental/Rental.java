package com.group13.ecopark_bicycle_parking.rental;

import com.group13.ecopark_bicycle_parking.bicycle.Bike;
import com.group13.ecopark_bicycle_parking.station.Station;
import com.group13.ecopark_bicycle_parking.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Rental {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer rentalId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bike_id", nullable = false)
	private Bike bike;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "start_station_id", nullable = false)
	private Station startStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "end_station_id")
	private Station endStation;

	private LocalDateTime reservedAt;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@Column(precision = 10, scale = 2)
	private BigDecimal rentalFee;

	@Column(precision = 10, scale = 2)
	private BigDecimal penaltyFee;

	private Integer discount;

	@Column(precision = 10, scale = 2)
	private BigDecimal totalFee;

	private String status;
}
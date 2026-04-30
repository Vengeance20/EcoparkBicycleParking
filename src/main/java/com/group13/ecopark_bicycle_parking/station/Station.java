package com.group13.ecopark_bicycle_parking.station;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;

@Entity
@Table(name = "stations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE stations SET is_deleted = true WHERE station_id=?")
@Where(clause = "is_deleted = false")
public class Station {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer stationId;

	@Column(unique = true, nullable = false)
	private String name;

	@Column(nullable = false, precision = 10, scale = 8)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 11, scale = 8)
	private BigDecimal longitude;

	@Column(nullable = false)
	private Integer capacity;

	private String status;

	@Column(nullable = false)
	private boolean isDeleted = false;
}
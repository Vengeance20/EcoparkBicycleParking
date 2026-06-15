package com.group13.ecopark_bicycle_parking.bicycle;

import com.group13.ecopark_bicycle_parking.station.Station;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "bikes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE bikes SET is_deleted = true WHERE bike_id=?")
@Where(clause = "is_deleted = false")
public class Bike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bikeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private BikeCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id")
	private Station station;

	@Column(unique = true, nullable = false)
	private String bikeCode;

	private String status;

	@Column(nullable = false)
	private boolean isDeleted = false;
}
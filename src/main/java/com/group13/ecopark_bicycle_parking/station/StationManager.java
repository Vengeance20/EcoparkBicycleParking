package com.group13.ecopark_bicycle_parking.station;

import com.group13.ecopark_bicycle_parking.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "station_managers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StationManager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer assignmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id", nullable = false)
	private User manager;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id", nullable = false)
	private Station station;
}

package com.group13.ecopark_bicycle_parking;

import jakarta.persistence.*;

@Entity
@Table (name = "station_managers")
public class StationManager {
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long assignment_id;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "manager_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "station_id")
    private Station station;

	public Long getAssignment_id() {
		return assignment_id;
	}

	public void setAssignment_id(Long assignment_id) {
		this.assignment_id = assignment_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}
   
}

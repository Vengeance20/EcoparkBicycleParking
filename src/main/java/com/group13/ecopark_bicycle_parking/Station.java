package com.group13.ecopark_bicycle_parking;

import jakarta.persistence.*;

@Entity // Báo cho Spring Boot biết: "Hãy biến class này thành 1 Bảng trong MySQL"
@Table(name = "stations") // Đặt tên cho bảng trong CSDL là 'bicycles'
public class Station {
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private Double latitude;
    
    private Double longitude;
    
    private Long capacity;
    
    @Column(nullable = false)
    private String status; // Trạng thái: "Mở cửa", "Đóng cửa"

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Long getCapacity() {
		return capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
}
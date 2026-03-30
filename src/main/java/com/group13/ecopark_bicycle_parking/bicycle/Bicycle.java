package com.group13.ecopark_bicycle_parking.bicycle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group13.ecopark_bicycle_parking.station.Station;

import jakarta.persistence.*;

@Entity // Báo cho Spring Boot biết: "Hãy biến class này thành 1 Bảng trong MySQL"
@Table(name = "bicycles") // Đặt tên cho bảng trong CSDL là 'bicycles'
public class Bicycle {

    @Id // Đánh dấu đây là Khóa chính (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng số ID (1, 2, 3...)
    private Long bike_id;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "station_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Station station; 
    
    // Mã xe hiển thị cho người dùng (Ví dụ: ECO-001)
    @Column(unique = true, nullable = false) 
    private String bike_code;
    
    @Column(nullable = false)
    private String bike_type;

    private String status;
    
	public String getBikeCode() {
		return bike_code;
	}

	public void setBikeCode(String bikeCode) {
		this.bike_code = bikeCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getBike_id() {
		return bike_id;
	}

	public void setBike_id(Long bike_id) {
		this.bike_id = bike_id;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getBikeType() {
		return bike_type;
	}

	public void setBikeType(String bike_type) {
		this.bike_type = bike_type;
	}

}

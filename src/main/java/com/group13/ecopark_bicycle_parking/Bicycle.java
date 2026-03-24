package com.group13.ecopark_bicycle_parking;

import jakarta.persistence.*;

@Entity // Báo cho Spring Boot biết: "Hãy biến class này thành 1 Bảng trong MySQL"
@Table(name = "bicycles") // Đặt tên cho bảng trong CSDL là 'bicycles'
public class Bicycle {

    @Id // Đánh dấu đây là Khóa chính (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng số ID (1, 2, 3...)
    private Long bike_id;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "station_id")
    private Station station; 
    
    // Mã xe hiển thị cho người dùng (Ví dụ: ECO-001)
    @Column(unique = true, nullable = false) 
    private String bikeCode;
    
    @Column(nullable = false)
    private String bikeType;

    // Trạng thái xe: "Đang hoạt động", "Đang thuê", "Cần sửa chữa"
    @Column(nullable = false)
    private String status;
    
	public String getBikeCode() {
		return bikeCode;
	}

	public void setBikeCode(String bikeCode) {
		this.bikeCode = bikeCode;
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
		return bikeType;
	}

	public void setBikeType(String bikeType) {
		this.bikeType = bikeType;
	}

}

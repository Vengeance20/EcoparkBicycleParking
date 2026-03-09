package com.group13.ecopark_bicycle_parking;

import jakarta.persistence.*;
import lombok.Data;

@Entity // Báo cho Spring Boot biết: "Hãy biến class này thành 1 Bảng trong MySQL"
@Table(name = "bicycles") // Đặt tên cho bảng trong CSDL là 'bicycles'
@Data // Của Lombok: Tự động tạo hàm get/set ẩn, giúp code siêu ngắn gọn
public class Bicycle {

    @Id // Đánh dấu đây là Khóa chính (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng số ID (1, 2, 3...)
    private Long id;

    // Mã xe hiển thị cho người dùng (Ví dụ: ECO-001)
    @Column(unique = true, nullable = false) 
    private String bikeCode;

    // Trạng thái xe: "Đang hoạt động", "Đang thuê", "Cần sửa chữa"
    @Column(nullable = false)
    private String status;
    
    // Thuộc bãi đỗ nào (Tạm thời lưu dạng text, sau này sẽ tạo bảng Station và liên kết sau)
    private String stationName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

}

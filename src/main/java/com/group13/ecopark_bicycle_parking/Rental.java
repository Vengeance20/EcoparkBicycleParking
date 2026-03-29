package com.group13.ecopark_bicycle_parking;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "rentals")
public class Rental {
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long rental_id;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "bike_id")
    private Bicycle bike;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "start_station_id")
    private Station start_station;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "end_station_id")
    private Station end_station; //NULL cho đến khi trả xe
    
    private LocalDateTime reserved_at;
    
    private LocalDateTime start_time; //Thời gian bắt đầu đi xe
    
    private LocalDateTime end_time; //Thời gian hoàn thành thuê xe
    
    private Double rental_fee; //Giá thuê gốc
    
    private Double penalty_fee; //Giá phạt nếu bom hàng
    
    private Long discount; // Phần trăm giảm giá
    
    private Double total_fee;
    
    private String status; //Trạng thái thuê: "Đặt trước, Đang thuê, Hoàn thành, Hủy"

	public Long getRental_id() {
		return rental_id;
	}

	public void setRental_id(Long rental_id) {
		this.rental_id = rental_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Bicycle getBike() {
		return bike;
	}

	public void setBike(Bicycle bike) {
		this.bike = bike;
	}

	public Station getStart_station() {
		return start_station;
	}

	public void setStart_station(Station start_station) {
		this.start_station = start_station;
	}

	public Station getEnd_station() {
		return end_station;
	}

	public void setEnd_station(Station end_station) {
		this.end_station = end_station;
	}

	public LocalDateTime getReserved_at() {
		return reserved_at;
	}

	public void setReserved_at(LocalDateTime reserved_at) {
		this.reserved_at = reserved_at;
	}

	public LocalDateTime getStart_time() {
		return start_time;
	}

	public void setStart_time(LocalDateTime start_time) {
		this.start_time = start_time;
	}

	public LocalDateTime getEnd_time() {
		return end_time;
	}

	public void setEnd_time(LocalDateTime end_time) {
		this.end_time = end_time;
	}

	public Double getRental_fee() {
		return rental_fee;
	}

	public void setRental_fee(Double rental_fee) {
		this.rental_fee = rental_fee;
	}

	public Double getPenalty_fee() {
		return penalty_fee;
	}

	public void setPenalty_fee(Double penalty_fee) {
		this.penalty_fee = penalty_fee;
	}

	public Long getDiscount() {
		return discount;
	}

	public void setDiscount(Long discount) {
		this.discount = discount;
	}

	public Double getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(Double total_fee) {
		this.total_fee = total_fee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}

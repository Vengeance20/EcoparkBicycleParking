package com.group13.ecopark_bicycle_parking;

import jakarta.persistence.*;

@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tenDangNhap; // Tên tài khoản không được trùng nhau

    @Column(nullable = false)
    private String matKhau;

    private String hoTen;
    
    private String vaiTro; // Sẽ lưu là "ADMIN" hoặc "KHACH_HANG"

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public String getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(String vaiTro) {
		this.vaiTro = vaiTro;
	}

}

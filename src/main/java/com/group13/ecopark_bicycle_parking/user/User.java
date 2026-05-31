package com.group13.ecopark_bicycle_parking.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id=?")
@Where(clause = "is_deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String nationalId;

    @Column(unique = true)
    private String phoneNumber;

    private String role;

    // Thêm trường này vào dưới các thông tin khác trong User.java
    @Column(name = "is_resident", nullable = false)
    private boolean isResident = false; // Mặc định khách mới đăng ký không phải là cư dân

    private String status;

    @Column(precision = 10, scale = 2)
    private BigDecimal walletBalance;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isDeleted = false;
}

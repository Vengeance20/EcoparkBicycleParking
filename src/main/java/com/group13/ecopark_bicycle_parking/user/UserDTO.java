package com.group13.ecopark_bicycle_parking.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank
        private String username;

        @NotBlank
        @Size(min = 6)
        private String password;

        @NotBlank
        private String fullName;

        @NotBlank
        @Email
        private String email;

        private String nationalId;
        private String phoneNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String fullName;

        @Email
        private String email;

        private String nationalId;
        private String phoneNumber;

        // 🔴 THÊM TRƯỜNG NÀY ĐỂ FRONTEND GỬI MÃ CƯ DÂN LÊN
        private String residentCode;
    }

    // 🔴 XÓA VerifyCardRequest VÌ ĐÃ GỘP VÀO UPDATE PROFILE

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Integer userId;
        private String username;
        private String fullName;
        private String email;
        private String nationalId;
        private String phoneNumber;
        private String role;
        private BigDecimal walletBalance;

        @JsonProperty("isLocked")
        private boolean isLocked;

        // 🔴 THÊM 2 TRƯỜNG NÀY ĐỂ FRONTEND BIẾT USER LÀ CƯ DÂN VÀ ĐƯỢC GIẢM BAO NHIÊU %
        @JsonProperty("isResident")
        private boolean isResident;

        private double discount; // Sẽ là 0.4 nếu là cư dân, 0.0 nếu không phải
    }
}
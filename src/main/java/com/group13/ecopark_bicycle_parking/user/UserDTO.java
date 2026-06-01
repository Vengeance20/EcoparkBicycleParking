package com.group13.ecopark_bicycle_parking.user;

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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyCardRequest {
        @NotBlank
        private String cardUserId;
    }

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
        private String status;
        private BigDecimal walletBalance;
        private boolean isResident;
        private String residentCardId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManagerRequest {
        private String mode;
        private Integer existingUserId;
        private String username;
        private String password;
        private String fullName;
        private String email;
        private String nationalId;
        private String phoneNumber;
        private Integer stationId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManagerResponse {
        private Integer userId;
        private String username;
        private String fullName;
        private String email;
        private String role;
        private String status;
        private Integer stationId;
        private String stationName;
        private String message;
    }
}

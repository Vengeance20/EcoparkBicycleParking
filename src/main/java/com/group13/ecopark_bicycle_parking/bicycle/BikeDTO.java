package com.group13.ecopark_bicycle_parking.bicycle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class BikeDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String bikeCode;
        private Integer categoryId;
        private Integer stationId; // 🔴 THÊM: Admin cần chọn trạm khi thêm xe
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String bikeCode;
        private Integer categoryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private String status; // AVAILABLE, MAINTENANCE, IN_USE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Integer bikeId;
        private String bikeCode;
        private Integer categoryId;
        private String categoryName; // 🔴 THÊM: Hiển thị tên loại xe
        private BigDecimal baseFee;
        private BigDecimal extraFee;
        private Integer stationId;
        private String stationName;  // 🔴 THÊM: Hiển thị tên trạm
        private String status;
    }
}
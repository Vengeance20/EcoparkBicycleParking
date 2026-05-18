package com.group13.ecopark_bicycle_parking.rental;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalDTO {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RentRequest {
        private Integer userId;     // ID người đang dùng app
        private String bikeCode;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RentResponse {
        private Integer rentalId;
        private String bikeCode;
        private LocalDateTime startTime;
        private String message;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RentalHistoryResponse {
        private Integer rentalId;
        private String bikeCode;
        private String startStationName;
        private String endStationName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal rentalFee;
        private BigDecimal penaltyFee;
        private Integer discount;
        private BigDecimal totalFee;
        private String status;
    }
}

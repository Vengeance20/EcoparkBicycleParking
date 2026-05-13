package com.group13.ecopark_bicycle_parking.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

public class PaymentDTO {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TopupRequest {
        private Integer userId;
        private BigDecimal amountVnd; // Số tiền thật khách nạp (VNĐ)
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TopupResponse {
        private Integer transactionId;
        private BigDecimal pointsAdded;     // Số Điểm được cộng
        private BigDecimal currentBalance;  // Tổng số dư hiện tại
        private String message;
    }
}
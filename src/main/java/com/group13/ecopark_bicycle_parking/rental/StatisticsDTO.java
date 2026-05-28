package com.group13.ecopark_bicycle_parking.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StatisticsDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsResponse {
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean hasData;
        private Long rentalRequests;
        private Long completedRentals;
        private Long uniqueCustomers;
        private BigDecimal totalRevenue;
        private BigDecimal totalRentalFee;
        private BigDecimal totalPenaltyFee;
        private BigDecimal averageRevenuePerRental;
        private Long walletTransactionCount;
        private BigDecimal walletNetAmount;
        private BigDecimal totalTopUp;
        private BigDecimal totalRentalPayment;
        private BigDecimal totalRentalDeposit;
        private BigDecimal totalRefund;
        private BigDecimal totalExtraFee;
        private List<DailyRevenue> revenueTrend;
        private List<StationRevenue> topStations;
    }
}

package com.group13.ecopark_bicycle_parking.rental;

import com.group13.ecopark_bicycle_parking.user.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class StatisticsService {

    private final RentalRepository rentalRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public StatisticsService(
            RentalRepository rentalRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional(readOnly = true)
    public StatisticsDTO.StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate) {
        validateCriteria(startDate, endDate);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        Long rentalRequests = rentalRepository.countRentalRequests(start, end);
        RentalAggregate rentalAggregate = rentalRepository.queryRentalStatistics(start, end);
        WalletAggregate walletAggregate = walletTransactionRepository.queryWalletStatistics(start, end);
        List<DailyRevenue> revenueTrend = rentalRepository.queryDailyRevenue(start, end);
        List<StationRevenue> topStations = rentalRepository.queryTopStations(start, end);

        Long completedRentals = rentalAggregate.getCompletedRentals();
        Long walletTransactions = walletAggregate.getWalletTransactionCount();
        boolean hasData = zeroIfNull(rentalRequests) > 0
                || zeroIfNull(completedRentals) > 0
                || zeroIfNull(walletTransactions) > 0
                || !revenueTrend.isEmpty()
                || !topStations.isEmpty();

        return StatisticsDTO.StatisticsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .hasData(hasData)
                .rentalRequests(zeroIfNull(rentalRequests))
                .completedRentals(zeroIfNull(completedRentals))
                .uniqueCustomers(zeroIfNull(rentalAggregate.getUniqueCustomers()))
                .totalRevenue(zeroIfNull(rentalAggregate.getTotalRevenue()))
                .totalRentalFee(zeroIfNull(rentalAggregate.getTotalRentalFee()))
                .totalPenaltyFee(zeroIfNull(rentalAggregate.getTotalPenaltyFee()))
                .averageRevenuePerRental(toBigDecimal(rentalAggregate.getAverageRevenuePerRental()))
                .walletTransactionCount(zeroIfNull(walletTransactions))
                .walletNetAmount(zeroIfNull(walletAggregate.getWalletNetAmount()))
                .totalTopUp(zeroIfNull(walletAggregate.getTotalTopUp()))
                .totalRentalPayment(zeroIfNull(walletAggregate.getTotalRentalPayment()))
                .totalRentalDeposit(zeroIfNull(walletAggregate.getTotalRentalDeposit()))
                .totalRefund(zeroIfNull(walletAggregate.getTotalRefund()))
                .totalExtraFee(zeroIfNull(walletAggregate.getTotalExtraFee()))
                .revenueTrend(revenueTrend)
                .topStations(topStations)
                .build();
    }

    private void validateCriteria(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
    }

    private Long zeroIfNull(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? BigDecimal.ZERO : BigDecimal.valueOf(value);
    }
}

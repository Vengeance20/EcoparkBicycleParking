package com.group13.ecopark_bicycle_parking.user;

import com.group13.ecopark_bicycle_parking.rental.WalletAggregate; // PHẢI CÓ DÒNG NÀY
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {

    @Query(value = "SELECT COUNT(*) as walletTransactionCount, " +
            "IFNULL(SUM(CASE WHEN amount > 0 THEN amount ELSE 0 END), 0) - IFNULL(SUM(CASE WHEN amount < 0 THEN ABS(amount) ELSE 0 END), 0) as walletNetAmount, " +
            "IFNULL(SUM(CASE WHEN transaction_type LIKE '%TOPUP%' THEN amount ELSE 0 END), 0) as totalTopUp, " +
            "IFNULL(SUM(CASE WHEN transaction_type LIKE '%PAYMENT%' OR transaction_type LIKE '%RENTAL_DEPOSIT%' THEN ABS(amount) ELSE 0 END), 0) as totalRentalPayment, " +
            "IFNULL(SUM(CASE WHEN transaction_type = 'RENTAL_DEPOSIT' THEN ABS(amount) ELSE 0 END), 0) as totalRentalDeposit, " +
            "IFNULL(SUM(CASE WHEN transaction_type LIKE '%REFUND%' THEN amount ELSE 0 END), 0) as totalRefund, " +
            "IFNULL(SUM(CASE WHEN transaction_type LIKE '%EXTRA%' THEN ABS(amount) ELSE 0 END), 0) as totalExtraFee " +
            "FROM wallet_transactions WHERE created_at BETWEEN :start AND :end", nativeQuery = true)
    WalletAggregate queryWalletStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
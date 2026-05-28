package com.group13.ecopark_bicycle_parking.user;

import com.group13.ecopark_bicycle_parking.rental.WalletAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {

    @Query("""
            select new com.group13.ecopark_bicycle_parking.rental.WalletAggregate(
                count(w),
                coalesce(sum(w.amount), 0),
                coalesce(sum(case when w.transactionType = 'TOPUP' then w.amount else 0 end), 0),
                coalesce(sum(case when w.transactionType = 'RENTAL_PAYMENT' then w.amount else 0 end), 0),
                coalesce(sum(case when w.transactionType = 'RENTAL_DEPOSIT' then w.amount else 0 end), 0),
                coalesce(sum(case when w.transactionType = 'REFUND_SURPLUS' then w.amount else 0 end), 0),
                coalesce(sum(case when w.transactionType = 'RENTAL_EXTRA_FEE' then w.amount else 0 end), 0)
            )
            from WalletTransaction w
            where w.createdAt between :startDate and :endDate
            """)
    WalletAggregate queryWalletStatistics(LocalDateTime startDate, LocalDateTime endDate);
}

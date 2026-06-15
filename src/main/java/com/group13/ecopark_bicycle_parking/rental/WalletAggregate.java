package com.group13.ecopark_bicycle_parking.rental;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletAggregate {
    private Long walletTransactionCount;
    private BigDecimal walletNetAmount;
    private BigDecimal totalTopUp;
    private BigDecimal totalRentalPayment;
    private BigDecimal totalRentalDeposit;
    private BigDecimal totalRefund;
    private BigDecimal totalExtraFee;
}

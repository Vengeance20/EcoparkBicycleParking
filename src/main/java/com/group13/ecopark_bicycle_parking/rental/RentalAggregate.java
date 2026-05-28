package com.group13.ecopark_bicycle_parking.rental;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalAggregate {
    private Long completedRentals;
    private BigDecimal totalRevenue;
    private BigDecimal totalRentalFee;
    private BigDecimal totalPenaltyFee;
    private Double averageRevenuePerRental;
    private Long uniqueCustomers;

    public RentalAggregate(
            Long completedRentals,
            BigDecimal totalRevenue,
            BigDecimal totalRentalFee,
            BigDecimal totalPenaltyFee,
            Number averageRevenuePerRental,
            Long uniqueCustomers
    ) {
        this.completedRentals = completedRentals;
        this.totalRevenue = totalRevenue;
        this.totalRentalFee = totalRentalFee;
        this.totalPenaltyFee = totalPenaltyFee;
        this.averageRevenuePerRental = averageRevenuePerRental == null ? null : averageRevenuePerRental.doubleValue();
        this.uniqueCustomers = uniqueCustomers;
    }
}

package com.group13.ecopark_bicycle_parking.rental;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenue {
    private String date;
    private Long completedRentals;
    private BigDecimal totalRevenue;
    private Double averageRevenue;

    public DailyRevenue(Object date, Long completedRentals, BigDecimal totalRevenue, Number averageRevenue) {
        this.date = date == null ? null : date.toString();
        this.completedRentals = completedRentals;
        this.totalRevenue = totalRevenue;
        this.averageRevenue = averageRevenue == null ? null : averageRevenue.doubleValue();
    }
}

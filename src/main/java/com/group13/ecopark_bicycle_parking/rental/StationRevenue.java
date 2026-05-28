package com.group13.ecopark_bicycle_parking.rental;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationRevenue {
    private Integer stationId;
    private String stationName;
    private Long completedRentals;
    private BigDecimal totalRevenue;
}

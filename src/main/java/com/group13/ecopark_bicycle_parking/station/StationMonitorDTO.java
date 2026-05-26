package com.group13.ecopark_bicycle_parking.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationMonitorDTO {

    private Integer stationId;
    private String stationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer capacity;
    private String stationStatus;
    private Long totalVehicles;
    private Long availableVehicles;
    private Long inUseVehicles;
    private Long maintenanceVehicles;
}

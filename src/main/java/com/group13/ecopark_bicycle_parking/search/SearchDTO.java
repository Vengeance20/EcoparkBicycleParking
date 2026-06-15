package com.group13.ecopark_bicycle_parking.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class SearchDTO {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StationResponse {
        private Integer stationId;
        private String name;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Integer capacity;
        private String status;
        private long totalBikeCount;
        private long availableBikeCount;
        private long availableSlotCount;
        private Double distanceKm;
        private List<CategoryCountResponse> availableBikeCountsByCategory;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StationDetailResponse {
        private StationResponse station;
        private List<BikeResponse> bikes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class BikeResponse {
        private Integer bikeId;
        private String bikeCode;
        private String category;
        private String status;
        private Integer stationId;
        private String stationName;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CategoryCountResponse {
        private String category;
        private long count;
    }
}

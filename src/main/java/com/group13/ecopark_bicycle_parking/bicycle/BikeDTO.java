package com.group13.ecopark_bicycle_parking.bicycle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BikeDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String bikeCode;
        private Integer categoryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String bikeCode;
        private Integer categoryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private String status; // AVAILABLE, MAINTENANCE, INACTIVE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Integer bikeId;
        private String bikeCode;
        private Integer categoryId;
        private Integer stationId;
        private String status;
    }
}

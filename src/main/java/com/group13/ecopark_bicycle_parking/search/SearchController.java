package com.group13.ecopark_bicycle_parking.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/apiv1/search")
public class SearchController {

    @Autowired private SearchService searchService;

    @GetMapping("/stations")
    public ResponseEntity<?> searchStations(
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean availableOnly
    ) {
        try {
            return ResponseEntity.ok(searchService.searchStations(latitude, longitude, radiusKm, keyword, availableOnly));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stations/{stationId}")
    public ResponseEntity<?> getStationDetail(
            @PathVariable Integer stationId,
            @RequestParam(required = false) String bikeStatus
    ) {
        try {
            return ResponseEntity.ok(searchService.getStationDetail(stationId, bikeStatus));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stations/{stationId}/available-bikes")
    public ResponseEntity<?> getAvailableBikesByStation(@PathVariable Integer stationId) {
        try {
            return ResponseEntity.ok(searchService.getAvailableBikesByStation(stationId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

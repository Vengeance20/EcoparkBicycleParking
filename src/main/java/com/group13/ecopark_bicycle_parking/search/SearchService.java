package com.group13.ecopark_bicycle_parking.search;

import com.group13.ecopark_bicycle_parking.bicycle.Bike;
import com.group13.ecopark_bicycle_parking.bicycle.BikeRepository;
import com.group13.ecopark_bicycle_parking.station.Station;
import com.group13.ecopark_bicycle_parking.station.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final String AVAILABLE_STATUS = "AVAILABLE";
    private static final double EARTH_RADIUS_KM = 6371.0088;

    @Autowired private StationRepository stationRepository;
    @Autowired private BikeRepository bikeRepository;

    @Transactional(readOnly = true)
    public List<SearchDTO.StationResponse> searchStations(
            BigDecimal latitude,
            BigDecimal longitude,
            Double radiusKm,
            String keyword,
            Boolean availableOnly
    ) {
        validateLocation(latitude, longitude);
        validateRadius(radiusKm);

        String normalizedKeyword = normalize(keyword);
        boolean requireAvailableBike = Boolean.TRUE.equals(availableOnly);
        boolean hasLocation = latitude != null && longitude != null;

        return stationRepository.findAll().stream()
                .map(station -> toStationResponse(station, latitude, longitude))
                .filter(station -> normalizedKeyword == null
                        || station.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .filter(station -> radiusKm == null
                        || (station.getDistanceKm() != null && station.getDistanceKm() <= radiusKm))
                .filter(station -> !requireAvailableBike || station.getAvailableBikeCount() > 0)
                .sorted(hasLocation
                        ? Comparator.comparing(SearchDTO.StationResponse::getDistanceKm)
                        : Comparator.comparing(SearchDTO.StationResponse::getName))
                .toList();
    }

    @Transactional(readOnly = true)
    public SearchDTO.StationDetailResponse getStationDetail(Integer stationId, String bikeStatus) {
        Station station = stationRepository.findByStationIdAndIsDeletedFalse(stationId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bãi đỗ."));

        List<Bike> bikes = normalize(bikeStatus) == null
                ? bikeRepository.findByStationStationIdAndIsDeletedFalse(stationId)
                : bikeRepository.findByStationStationIdAndStatusAndIsDeletedFalse(stationId, bikeStatus.trim().toUpperCase(Locale.ROOT));

        return new SearchDTO.StationDetailResponse(
                toStationResponse(station, null, null),
                bikes.stream()
                        .sorted(Comparator.comparing(Bike::getBikeCode))
                        .map(this::toBikeResponse)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<SearchDTO.BikeResponse> getAvailableBikesByStation(Integer stationId) {
        Station station = stationRepository.findByStationIdAndIsDeletedFalse(stationId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bãi đỗ."));

        return bikeRepository.findByStationStationIdAndStatusAndIsDeletedFalse(station.getStationId(), AVAILABLE_STATUS)
                .stream()
                .sorted(Comparator.comparing(Bike::getBikeCode))
                .map(this::toBikeResponse)
                .toList();
    }

    private void validateRadius(Double radiusKm) {
        if (radiusKm != null && radiusKm < 0) {
            throw new RuntimeException("Lỗi: radiusKm không được nhỏ hơn 0.");
        }
    }

    private SearchDTO.StationResponse toStationResponse(Station station, BigDecimal latitude, BigDecimal longitude) {
        long totalBikeCount = bikeRepository.countByStationStationIdAndIsDeletedFalse(station.getStationId());
        long availableBikeCount = bikeRepository.countByStationStationIdAndStatusAndIsDeletedFalse(station.getStationId(), AVAILABLE_STATUS);
        long availableSlotCount = Math.max(0, station.getCapacity() - totalBikeCount);

        List<SearchDTO.CategoryCountResponse> categoryCounts = bikeRepository
                .findByStationStationIdAndStatusAndIsDeletedFalse(station.getStationId(), AVAILABLE_STATUS)
                .stream()
                .collect(Collectors.groupingBy(bike -> bike.getCategory().getName(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new SearchDTO.CategoryCountResponse(entry.getKey(), entry.getValue()))
                .toList();

        Double distanceKm = null;
        if (latitude != null && longitude != null) {
            distanceKm = roundDistance(calculateDistanceKm(
                    latitude.doubleValue(),
                    longitude.doubleValue(),
                    station.getLatitude().doubleValue(),
                    station.getLongitude().doubleValue()
            ));
        }

        return new SearchDTO.StationResponse(
                station.getStationId(),
                station.getName(),
                station.getLatitude(),
                station.getLongitude(),
                station.getCapacity(),
                station.getStatus(),
                totalBikeCount,
                availableBikeCount,
                availableSlotCount,
                distanceKm,
                categoryCounts
        );
    }

    private SearchDTO.BikeResponse toBikeResponse(Bike bike) {
        Station station = bike.getStation();
        return new SearchDTO.BikeResponse(
                bike.getBikeId(),
                bike.getBikeCode(),
                bike.getCategory().getName(),
                bike.getStatus(),
                station == null ? null : station.getStationId(),
                station == null ? null : station.getName()
        );
    }

    private void validateLocation(BigDecimal latitude, BigDecimal longitude) {
        if ((latitude == null) != (longitude == null)) {
            throw new RuntimeException("Lỗi: Cần truyền cả latitude và longitude.");
        }

        if (latitude != null && (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0)) {
            throw new RuntimeException("Lỗi: latitude phải nằm trong khoảng -90 đến 90.");
        }

        if (longitude != null && (longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new RuntimeException("Lỗi: longitude phải nằm trong khoảng -180 đến 180.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private Double roundDistance(double distanceKm) {
        return BigDecimal.valueOf(distanceKm)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Integer> {
    Optional<Bike> findByBikeCodeAndIsDeletedFalse(String bikeCode);

    List<Bike> findByStationStationIdAndIsDeletedFalse(Integer stationId);

    List<Bike> findByStationStationIdAndStatusAndIsDeletedFalse(Integer stationId, String status);

    long countByStationStationIdAndIsDeletedFalse(Integer stationId);

    long countByStationStationIdAndStatusAndIsDeletedFalse(Integer stationId, String status);
    
    // Thêm hàm đếm số xe đang đỗ tại bãi
    int countByStationStationId(Integer stationId);
}

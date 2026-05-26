package com.group13.ecopark_bicycle_parking.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {
    Optional<Station> findByStationIdAndIsDeletedFalse(Integer stationId);

    // Tìm trạm theo tên bãi đỗ xe
    Optional<Station> findByName(String name);
}

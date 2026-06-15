package com.group13.ecopark_bicycle_parking.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {
    Optional<Station> findByStationIdAndIsDeletedFalse(Integer stationId);
    Optional<Station> findByName(String name);

    // 🔴 THÊM DÒNG NÀY VÀO: Query lấy dữ liệu thống kê cho Admin
    @Query("""
            select new com.group13.ecopark_bicycle_parking.station.StationMonitorDTO(
                s.stationId,
                s.name,
                s.latitude,
                s.longitude,
                s.capacity,
                s.status,
                count(b.bikeId),
                sum(case when b.status = 'AVAILABLE' then 1 else 0 end),
                sum(case when b.status = 'IN_USE' then 1 else 0 end),
                sum(case when b.status = 'MAINTENANCE' then 1 else 0 end)
            )
            from Station s
            left join Bike b on b.station = s
            where s.isDeleted = false
            group by s.stationId, s.name, s.latitude, s.longitude, s.capacity, s.status
            """)
    java.util.List<StationMonitorDTO> findAllStationMonitorDataForAdmin();
}
package com.group13.ecopark_bicycle_parking.station;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationManagerRepository extends JpaRepository<StationManager, Integer> {

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
            from StationManager sm
            join sm.station s
            left join Bike b on b.station = s
            where sm.manager.userId = :managerId
            group by s.stationId, s.name, s.latitude, s.longitude, s.capacity, s.status
            """)
           
    List<StationMonitorDTO> findMonitorDataByManager(@Param("managerId") Integer managerId);
    // Tìm thông tin bãi xe được phân công dựa trên ID tài khoản Manager
    Optional<StationManager> findByManager_UserId(Integer managerId);
}

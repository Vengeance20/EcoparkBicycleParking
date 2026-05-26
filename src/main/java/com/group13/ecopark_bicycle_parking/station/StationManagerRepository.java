package com.group13.ecopark_bicycle_parking.station;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StationManagerRepository extends JpaRepository<StationManager, Integer> {
    // Tìm thông tin bãi xe được phân công dựa trên ID tài khoản Manager
    Optional<StationManager> findByManager_UserId(Integer managerId);
}
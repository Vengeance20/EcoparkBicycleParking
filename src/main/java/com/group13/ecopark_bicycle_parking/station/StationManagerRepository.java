package com.group13.ecopark_bicycle_parking.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StationManagerRepository extends JpaRepository<StationManager, Long> {
    
    // Tuyệt chiêu dấu gạch dưới "_"
    Optional<StationManager> findByUser_UserId(Long userId);
    
}
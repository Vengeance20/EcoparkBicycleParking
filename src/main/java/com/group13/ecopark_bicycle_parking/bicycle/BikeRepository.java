package com.group13.ecopark_bicycle_parking.bicycle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Integer> {
    Optional<Bike> findByBikeCodeAndIsDeletedFalse(String bikeCode);
}
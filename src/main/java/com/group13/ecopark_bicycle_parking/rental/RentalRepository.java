package com.group13.ecopark_bicycle_parking.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    // 1. Dùng cho Scheduler: Tìm các đơn "RESERVED" có thời gian đặt trước một mốc timeLimit
    List<Rental> findByStatusAndReservedAtBefore(String status, LocalDateTime timeLimit);

    // 2. Dùng cho API Nhận xe (Unlock): Tìm đúng chuyến đi của user đó, xe đó, đang chờ nhận
    Optional<Rental> findByUserUserIdAndBikeBikeCodeAndStatus(Integer userId, String bikeCode, String status);

    // 3. Dùng cho U7 - View History: Lấy lịch sử thuê xe của một user, mới nhất trước
    List<Rental> findAllByUserUserIdOrderByRentalIdDesc(Integer userId);

}

package com.group13.ecopark_bicycle_parking.dashboard;

import com.group13.ecopark_bicycle_parking.bicycle.BikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/apiv1/admin/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private BikeRepository bikeRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        // 1. Tổng số xe (chưa bị xóa mềm)
        long totalBikes = bikeRepository.countByIsDeletedFalse();

        // 2. Xe đang hoạt động (Sẵn sàng + Đang thuê + Đã đặt trước)
        long available = bikeRepository.countByStatusAndIsDeletedFalse("AVAILABLE");
        long inUse = bikeRepository.countByStatusAndIsDeletedFalse("IN_USE");
        long reserved = bikeRepository.countByStatusAndIsDeletedFalse("RESERVED");
        long activeBikes = available + inUse + reserved;

        // 3. Xe đang bảo trì
        long maintenanceBikes = bikeRepository.countByStatusAndIsDeletedFalse("MAINTENANCE");

        return ResponseEntity.ok(Map.of(
                "totalBikes", totalBikes,
                "activeBikes", activeBikes,
                "maintenanceBikes", maintenanceBikes
        ));
    }
}
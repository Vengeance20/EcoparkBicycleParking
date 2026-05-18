package com.group13.ecopark_bicycle_parking.rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiv1/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping("/rent")
    public ResponseEntity<?> rentBike(@RequestBody RentalDTO.RentRequest request) {
        try {
            RentalDTO.RentResponse response = rentalService.rentBike(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Trong thực tế sẽ dùng ControllerAdvice để handle Exception,
            // nhưng code này giúp bắt nhanh lỗi trả về mã 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // API 1: Đặt xe trước (Khách ngồi ở nhà bấm)
    // ==========================================
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveBike(@RequestBody RentalDTO.RentRequest request) {
        try {
            // Gọi xuống hàm reserveBike ở tầng Service mà bạn vừa viết
            RentalDTO.RentResponse response = rentalService.reserveBike(request.getUserId(), request.getBikeCode());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // API 2: Nhận xe (Khách ra bãi quét mã)
    // ==========================================
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockBike(@RequestBody RentalDTO.RentRequest request) {
        try {
            // Gọi xuống hàm unlockBike ở tầng Service
            String message = rentalService.unlockBike(request.getUserId(), request.getBikeCode());
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // API 3: Xem lịch sử thuê xe
    // ==========================================
    @GetMapping("/history/{userKey}")
    public ResponseEntity<?> getRentalHistory(@PathVariable String userKey) {
        try {
            return ResponseEntity.ok(rentalService.getRentalHistory(userKey));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

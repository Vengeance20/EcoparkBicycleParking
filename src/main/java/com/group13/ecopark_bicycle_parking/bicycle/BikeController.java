package com.group13.ecopark_bicycle_parking.bicycle;

import com.group13.ecopark_bicycle_parking.security.JwtUtil;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiv1/vehicles")
public class BikeController {

    private final BikeService bikeService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public BikeController(BikeService bikeService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.bikeService = bikeService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // Tự động phân tích Token để lấy đúng ID của người dùng đang đăng nhập
    private Integer extractManagerIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Cảnh báo: Thẻ xác thực quyền truy cập trống hoặc không hợp lệ!");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản hợp lệ!"))
                .getUserId();
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@RequestBody BikeDTO.CreateRequest request,
                                           @RequestHeader("Authorization") String token) {
        try {
            Integer managerId = extractManagerIdFromToken(token);
            var response = bikeService.addVehicle(request, managerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<?> updateVehicle(@PathVariable Integer vehicleId,
                                           @RequestBody BikeDTO.UpdateRequest request,
                                           @RequestHeader("Authorization") String token) {
        try {
            Integer managerId = extractManagerIdFromToken(token);
            var response = bikeService.updateVehicle(vehicleId, request, managerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{vehicleId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer vehicleId,
                                          @RequestBody BikeDTO.StatusUpdateRequest request,
                                          @RequestHeader("Authorization") String token) {
        try {
            Integer managerId = extractManagerIdFromToken(token);
            var response = bikeService.updateVehicleStatus(vehicleId, request, managerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Integer vehicleId,
                                           @RequestHeader("Authorization") String token) {
        try {
            Integer managerId = extractManagerIdFromToken(token);
            bikeService.deleteVehicle(vehicleId, managerId);
            return ResponseEntity.ok("Xóa thông tin phương tiện khỏi bãi đỗ thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra sự cố hệ thống!");
        }
    }
}
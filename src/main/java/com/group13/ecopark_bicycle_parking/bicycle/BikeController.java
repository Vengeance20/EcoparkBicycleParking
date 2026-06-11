package com.group13.ecopark_bicycle_parking.bicycle;

import com.group13.ecopark_bicycle_parking.security.JwtUtil;
import com.group13.ecopark_bicycle_parking.station.StationRepository;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/apiv1/vehicles")
public class BikeController {

    private final BikeService bikeService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BikeCategoryRepository categoryRepository; // 🔴 THÊM
    private final StationRepository stationRepository;       // 🔴 THÊM

    public BikeController(BikeService bikeService, JwtUtil jwtUtil, UserRepository userRepository, BikeCategoryRepository categoryRepository, StationRepository stationRepository) {
        this.bikeService = bikeService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.stationRepository = stationRepository;
    }

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

    // ===== CÁC API CŨ CỦA MANAGER (Giữ nguyên) =====
    @PostMapping
    public ResponseEntity<?> createVehicle(@RequestBody BikeDTO.CreateRequest request, @RequestHeader("Authorization") String token) {
        try {
            Integer managerId = extractManagerIdFromToken(token);
            var response = bikeService.addVehicle(request, managerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PatchMapping("/{vehicleId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer vehicleId, @RequestBody BikeDTO.StatusUpdateRequest request, @RequestHeader("Authorization") String token) {
        try {
            Integer managerId = extractManagerIdFromToken(token);
            var response = bikeService.updateVehicleStatus(vehicleId, request, managerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    // ===== API PUBLIC & DÀNH CHO ADMIN =====

    @GetMapping("/public")
    public ResponseEntity<?> getAllVehicles() {
        return ResponseEntity.ok(bikeService.getAllVehicles());
    }

    // 🔴 THÊM API: Lấy danh sách Loại xe cho Dropdown
    @GetMapping("/admin/categories-dropdown")
    public ResponseEntity<?> getCategoriesDropdown() {
        return ResponseEntity.ok(categoryRepository.findAllCategories());
    }

    // 🔴 THÊM API: Lấy danh sách Trạm cho Dropdown
    @GetMapping("/admin/stations-dropdown")
    public ResponseEntity<?> getStationsDropdown() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

    // 🔴 THÊM API: Admin thêm xe mới (Không cần Token Manager)
    @PostMapping("/admin")
    public ResponseEntity<?> addVehicleByAdmin(@RequestBody BikeDTO.CreateRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bikeService.addVehicleByAdmin(request));
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    // 🔴 THÊM API: Admin đổi trạng thái xe (Không cần Token Manager)
    @PatchMapping("/admin/{vehicleId}/status")
    public ResponseEntity<?> updateStatusByAdmin(@PathVariable Integer vehicleId, @RequestBody BikeDTO.StatusUpdateRequest request) {
        try {
            return ResponseEntity.ok(bikeService.updateVehicleStatusByAdmin(vehicleId, request));
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}
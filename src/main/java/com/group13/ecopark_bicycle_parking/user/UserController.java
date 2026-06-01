package com.group13.ecopark_bicycle_parking.user;

import com.group13.ecopark_bicycle_parking.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/apiv1/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil; // Thêm máy in thẻ

    // Nhúng JwtUtil qua Constructor
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO.RegisterRequest userDTO) {
        try {
            return ResponseEntity.ok(userService.register(userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Integer userId) {
        try {
            // Frontend sẽ gọi vào đây mỗi khi mở trang Nạp Tiền
            return ResponseEntity.ok(userService.getUserProfile(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO.LoginRequest credentials) {
        try {
            // 1. Xác thực tài khoản (Kiểm tra email, password)
            UserDTO.UserResponse user = userService.authenticate(credentials);

            // 2. Cấp thẻ JWT Token (Dùng username làm định danh)
            String token = jwtUtil.generateToken(user.getUsername());

            // 3. Trả về cả thông tin User và Token
            return ResponseEntity.ok(Map.of(
                    "user", user,
                    "accessToken", token
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UserDTO.UpdateProfileRequest userDTO
    ) {
        try {
            return ResponseEntity.ok(userService.updateUserInfo(userId, userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-card/{userId}")
    public ResponseEntity<?> verifyCard(
            @PathVariable Integer userId,
            @Valid @RequestBody UserDTO.VerifyCardRequest request) {
        try {
            // Truyền cả userId và mã thẻ xuống Service
            return ResponseEntity.ok(userService.verifyResident(userId, request.getCardUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

package com.group13.ecopark_bicycle_parking.user;

import org.springframework.web.bind.MethodArgumentNotValidException;
import com.group13.ecopark_bicycle_parking.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apiv1/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // ==================== API CỦA USER ====================

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO.RegisterRequest userDTO) {
        try {
            return ResponseEntity.ok(userService.register(userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO.LoginRequest credentials) {
        try {
            UserDTO.UserResponse user = userService.authenticate(credentials);
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(Map.of("user", user, "accessToken", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔴 API NÀY SẼ XỬ LÝ CẢ CHỈNH SỬA PROFILE LẪN XÁC THỰC CƯ DÂN
    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer userId, @Valid @RequestBody UserDTO.UpdateProfileRequest userDTO) {
        try {
            return ResponseEntity.ok(userService.updateUserInfo(userId, userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔴 ĐÃ XÓA @PostMapping("/verify-card") VÌ ĐÃ GỘP VÀO UPDATE PROFILE

    // ==================== API CỦA ADMIN (QUẢN LÝ TÀI KHOẢN) ====================

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/lock")
    public ResponseEntity<?> lockUser(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.lockUser(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.unlockUser(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String newRole = body.get("role");
            return ResponseEntity.ok(userService.updateRole(id, newRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== XỬ LÝ LỖI ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex) {
        String message = "Lỗi cơ sở dữ liệu!";
        if (ex.getMessage().contains("username")) message = "Tên đăng nhập đã tồn tại!";
        else if (ex.getMessage().contains("email")) message = "Email đã tồn tại!";
        return ResponseEntity.badRequest().body(message);
    }
}
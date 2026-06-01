package com.group13.ecopark_bicycle_parking.user;

import com.group13.ecopark_bicycle_parking.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO.RegisterRequest userDTO) {
        try {
            return ResponseEntity.ok(userService.register(userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint để Admin tạo tài khoản Manager nhanh (UserManagement.html dùng)
    @PostMapping("/create-manager")
    public ResponseEntity<?> createManager(@Valid @RequestBody UserDTO.RegisterRequest userDTO) {
        try {
            return ResponseEntity.ok(userService.registerWithRole(userDTO, "MANAGER"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO.LoginRequest credentials) {
        try {
            UserDTO.UserResponse user = userService.authenticate(credentials);
            String token = jwtUtil.generateToken(user.getUsername());
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

    @PostMapping("/verify-card")
    public ResponseEntity<?> verifyCard(@Valid @RequestBody UserDTO.VerifyCardRequest request) {
        try {
            return ResponseEntity.ok(userService.verifyResident(request.getCardUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint lấy danh sách tất cả user (UserManagement.html dùng)
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDTO.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Admin xóa mềm user
    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("Đã xóa tài khoản thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        String message = "Lỗi cơ sở dữ liệu!";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("username")) message = "Tên đăng nhập đã tồn tại!";
            else if (ex.getMessage().contains("email")) message = "Email đã tồn tại!";
        }
        return ResponseEntity.badRequest().body(message);
    }
}

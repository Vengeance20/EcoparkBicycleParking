package com.group13.ecopark_bicycle_parking.user;

import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @PostMapping("/verify-card")
    public ResponseEntity<?> verifyCard(@Valid @RequestBody UserDTO.VerifyCardRequest request) {
        try {
            return ResponseEntity.ok(userService.verifyResident(request.getCardUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔴 HÀM MỚI: Bắt lỗi Validate (@NotBlank, @Size, @Email...) trả về message tiếng Việt
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

     @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex) {
        String message = "Lỗi cơ sở dữ liệu!";
        // Kiểm tra xem là trùng Username hay Email
        if (ex.getMessage().contains("username")) {
            message = "Tên đăng nhập đã tồn tại!";
        } else if (ex.getMessage().contains("email")) {
            message = "Email đã tồn tại!";
        }
        return ResponseEntity.badRequest().body(message);
    }
}

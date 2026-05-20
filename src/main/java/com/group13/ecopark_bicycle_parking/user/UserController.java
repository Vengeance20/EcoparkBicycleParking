package com.group13.ecopark_bicycle_parking.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
            return ResponseEntity.ok(userService.authenticate(credentials));
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
}

package com.group13.ecopark_bicycle_parking.account.controller;

import com.group13.ecopark_bicycle_parking.account.dto.*;
import com.group13.ecopark_bicycle_parking.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    // U1.1 - POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountResponse>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", accountService.register(req)));
    }

    // U1.2 - POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", accountService.login(req)));
    }

    // U1.4 - POST /api/auth/verify-card?cardId=xxx
    @PostMapping("/verify-card")
    public ResponseEntity<ApiResponse<AccountResponse>> verifyCard(@RequestParam String cardId) {
        return ResponseEntity.ok(ApiResponse.success("Card verified", accountService.verifyResidentCard(cardId)));
    }
}

package com.group13.ecopark_bicycle_parking.account.controller;

import com.group13.ecopark_bicycle_parking.account.dto.*;
import com.group13.ecopark_bicycle_parking.account.model.Account;
import com.group13.ecopark_bicycle_parking.account.repository.AccountRepository;
import com.group13.ecopark_bicycle_parking.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    // GET /api/accounts/profile  - lấy profile của mình
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AccountResponse>> getProfile(@AuthenticationPrincipal UserDetails ud) {
        Account current = accountRepository.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(ApiResponse.success("OK", accountService.getProfile(current.getId())));
    }

    // U1.3 - PUT /api/accounts/update-profile/{id}
    @PutMapping("/update-profile/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal UserDetails ud) {

        Account current = accountRepository.findByEmail(ud.getUsername()).orElseThrow();
        if (!current.getId().equals(id) && current.getRole() != Account.Role.ADMIN)
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));

        return ResponseEntity.ok(ApiResponse.success("Updated", accountService.updateProfile(id, req)));
    }
}

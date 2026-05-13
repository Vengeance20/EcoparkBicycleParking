package com.group13.ecopark_bicycle_parking.account.service;

import com.group13.ecopark_bicycle_parking.account.dto.*;
import com.group13.ecopark_bicycle_parking.account.exception.*;
import com.group13.ecopark_bicycle_parking.account.model.Account;
import com.group13.ecopark_bicycle_parking.account.repository.AccountRepository;
import com.group13.ecopark_bicycle_parking.account.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // U1.1 - Register
    @Transactional
    public AccountResponse register(RegisterRequest req) {
        if (accountRepository.existsByEmail(req.getEmail()))
            throw new EmailAlreadyExistsException("Email already taken: " + req.getEmail());

        Account account = Account.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .cardId(req.getCardId())
                .build();

        return AccountResponse.fromAccount(accountRepository.save(account));
    }

    // U1.2 - Login
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        var ud = userDetailsService.loadUserByUsername(req.getEmail());
        String token = jwtUtil.generateToken(ud);
        Account account = accountRepository.findByEmail(req.getEmail()).orElseThrow();

        return AuthResponse.builder()
                .token(token).email(account.getEmail()).fullName(account.getFullName())
                .message("Login successful").build();
    }

    // U1.3 - Update Profile
    @Transactional
    public AccountResponse updateProfile(Long id, UpdateProfileRequest req) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));

        if (req.getFullName() != null)    account.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) account.setPhoneNumber(req.getPhoneNumber());
        if (req.getAddress() != null)     account.setAddress(req.getAddress());

        return AccountResponse.fromAccount(accountRepository.save(account));
    }

    // U1.4 - Verify Resident Card
    public AccountResponse verifyResidentCard(String cardId) {
        Account account = accountRepository.findByCardId(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));
        return AccountResponse.fromAccount(account);
    }

    public AccountResponse getProfile(Long id) {
        return AccountResponse.fromAccount(accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id)));
    }
}

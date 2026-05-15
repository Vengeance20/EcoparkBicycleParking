package com.group13.ecopark_bicycle_parking.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDTO.UserResponse register(UserDTO.RegisterRequest userDTO) {
        String email = normalizeEmail(userDTO.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email taken");
        }

        User user = User.builder()
                .username(userDTO.getUsername().trim())
                .passwordHash(hashPassword(userDTO.getPassword()))
                .fullName(trimToNull(userDTO.getFullName()))
                .email(email)
                .nationalId(trimToNull(userDTO.getNationalId()))
                .phoneNumber(trimToNull(userDTO.getPhoneNumber()))
                .role("CUSTOMER")
                .walletBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserDTO.UserResponse authenticate(UserDTO.LoginRequest credentials) {
        User user = userRepository.findByEmail(normalizeEmail(credentials.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordMatches(credentials.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return toResponse(user);
    }

    @Transactional
    public UserDTO.UserResponse updateUserInfo(Integer userId, UserDTO.UpdateProfileRequest userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newEmail = normalizeNullableEmail(userDTO.getEmail());
        if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email taken");
        }

        if (userDTO.getFullName() != null) {
            user.setFullName(trimToNull(userDTO.getFullName()));
        }
        if (newEmail != null) {
            user.setEmail(newEmail);
        }
        if (userDTO.getNationalId() != null) {
            user.setNationalId(trimToNull(userDTO.getNationalId()));
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(trimToNull(userDTO.getPhoneNumber()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserDTO.UserResponse verifyResident(String cardUserId) {
        return userRepository.findByNationalId(cardUserId.trim())
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
    }

    private boolean passwordMatches(String rawPassword, String passwordHash) {
        return passwordHash.equals(hashPassword(rawPassword)) || passwordHash.equals(rawPassword);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot hash password", e);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizeNullableEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return normalizeEmail(email);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private UserDTO.UserResponse toResponse(User user) {
        return UserDTO.UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .nationalId(user.getNationalId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .walletBalance(user.getWalletBalance())
                .build();
    }
}

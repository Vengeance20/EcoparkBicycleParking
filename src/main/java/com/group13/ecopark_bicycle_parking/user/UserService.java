package com.group13.ecopark_bicycle_parking.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public UserService(UserRepository userRepository, WalletTransactionRepository walletTransactionRepository) {
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    // ==================== CÁC HÀM CỦ USER ====================

    @Transactional
    public UserDTO.UserResponse register(UserDTO.RegisterRequest userDTO) {
        String email = normalizeEmail(userDTO.getEmail());
        String username = userDTO.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        User user = User.builder()
                .username(username)
                .passwordHash(hashPassword(userDTO.getPassword()))
                .fullName(trimToNull(userDTO.getFullName()))
                .email(email)
                .nationalId(trimToNull(userDTO.getNationalId()))
                .phoneNumber(trimToNull(userDTO.getPhoneNumber()))
                .role("CUSTOMER")
                .walletBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .isLocked(false)
                .isResident(false) // Mặc định chưa phải cư dân
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

        if (user.isLocked()) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa. Vui lòng liên hệ Admin.");
        }

        return toResponse(user);
    }

    // 🔴 SỬA HÀM NÀY: CẬP NHẬT THÔNG TIN CÁ NHÂN VÀ XÁC THỰC CƯ DÂN
    @Transactional
    public UserDTO.UserResponse updateUserInfo(Integer userId, UserDTO.UpdateProfileRequest userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newEmail = normalizeNullableEmail(userDTO.getEmail());
        if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email taken");
        }

        if (userDTO.getFullName() != null) user.setFullName(trimToNull(userDTO.getFullName()));
        if (newEmail != null) user.setEmail(newEmail);
        if (userDTO.getNationalId() != null) user.setNationalId(trimToNull(userDTO.getNationalId()));
        if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(trimToNull(userDTO.getPhoneNumber()));

        // 🔴 LOGIC XÁC THỰC CƯ DÂN TẠI ĐÂY
        if (userDTO.getResidentCode() != null && !userDTO.getResidentCode().trim().isEmpty()) {
            if (verifyResidentCode(userId, userDTO.getResidentCode().trim())) {
                user.setResident(true); // Hợp lệ thì set true
            } else {
                // Ném lỗi để Frontend hiện thông báo "Mã cư dân không hợp lệ"
                throw new IllegalArgumentException("Mã cư dân không hợp lệ! Xác thực thất bại.");
            }
        }

        return toResponse(userRepository.save(user));
    }

    // 🔴 HÀM KIỂM TRA MÃ CƯ DÂN: ECO-{user_id}
    private boolean verifyResidentCode(Integer userId, String code) {
        // Hỗ trợ cả ECO-1 và ECO-01 cho user_id = 1
        String expectedCode1 = "ECO-" + userId;
        String expectedCode2 = String.format("ECO-%02d", userId);

        return expectedCode1.equalsIgnoreCase(code) || expectedCode2.equalsIgnoreCase(code);
    }

    // ==================== CÁC HÀM CỦA ADMIN ====================

    @Transactional(readOnly = true)
    public List<UserDTO.UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserDTO.UserResponse lockUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLocked(true);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserDTO.UserResponse unlockUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLocked(false);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserDTO.UserResponse updateRole(Integer userId, String newRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(newRole);
        return toResponse(userRepository.save(user));
    }

    // ==================== HÀM UTILS ====================

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

    private String normalizeEmail(String email) { return email.trim().toLowerCase(); }

    private String normalizeNullableEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return normalizeEmail(email);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    // 🔴 SỬA HÀM TO_RESPONSE: THÊM isResident VÀ discount
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
                .isLocked(user.isLocked())
                .isResident(user.isResident())
                .discount(user.isResident() ? 0.4 : 0.0) // Frontend lấy biến này nhân đơn giá
                .build();
    }
}
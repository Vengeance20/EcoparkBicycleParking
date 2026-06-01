package com.group13.ecopark_bicycle_parking.user;

import com.group13.ecopark_bicycle_parking.station.Station;
import com.group13.ecopark_bicycle_parking.station.StationManager;
import com.group13.ecopark_bicycle_parking.station.StationManagerRepository;
import com.group13.ecopark_bicycle_parking.station.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String MODE_CREATE = "CREATE";
    private static final String MODE_PROMOTE = "PROMOTE";

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final StationManagerRepository stationManagerRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(
            UserRepository userRepository,
            StationRepository stationRepository,
            StationManagerRepository stationManagerRepository
    ) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.stationManagerRepository = stationManagerRepository;
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
                .role(ROLE_CUSTOMER)
                .status(STATUS_ACTIVE)
                .walletBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return toResponse(userRepository.save(user));
    }

    // Phương thức tạo tài khoản Manager trực tiếp (dùng cho /apiv1/auth/create-manager)
    @Transactional
    public UserDTO.UserResponse registerWithRole(UserDTO.RegisterRequest userDTO, String role) {
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
                .role(role)
                .status(STATUS_ACTIVE)
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

    // 2. Viết lại hàm verifyResident để tự động nâng cấp cư dân
    @Transactional
    public UserDTO.UserResponse verifyResident(String cardUserId) {
        // Overload 1 tham số: dùng khi controller chỉ gửi cardUserId
        // Tìm user theo cardUserId nếu đã liên kết, hoặc throw lỗi yêu cầu userId
        throw new IllegalArgumentException("Thiếu userId. Vui lòng gửi kèm userId trong request.");
    }

    @Transactional
    public UserDTO.UserResponse verifyResident(Integer userId, String cardUserId) {
        // Tìm user đang đăng nhập
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản!"));

        // (Tùy chọn) Kiểm tra xem mã thẻ này đã có ai dùng chưa
        if (userRepository.existsByNationalId(cardUserId) && !cardUserId.equals(user.getNationalId())) {
            throw new IllegalArgumentException("Mã thẻ này đã được liên kết với một tài khoản khác!");
        }

        // Cập nhật quyền lợi cư dân (is_resident = 1)
        user.setResident(true);
        user.setResidentCardId(cardUserId);

        // Lưu xuống Database
        userRepository.save(user);

        // Trả về thông tin User mới nhất để Frontend cập nhật giao diện
        return toResponse(user);
    }

    // Hàm này lấy User từ DB và đóng gói lại thành UserResponse (có chứa walletBalance)
    public UserDTO.UserResponse getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
        return toResponse(user); // Dùng lại hàm toResponse bạn đã viết lúc trước
    }

    // Lấy toàn bộ danh sách user (Admin dùng)
    @Transactional(readOnly = true)
    public List<UserDTO.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Xóa mềm user (Admin dùng)
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
        userRepository.delete(user); // @SQLDelete sẽ set is_deleted = true
    }

    @Transactional
    public UserDTO.ManagerResponse createOrPromoteManager(Integer adminId, UserDTO.ManagerRequest request) {
        validateAdmin(adminId);
        validateManagerRequest(request);

        Station station = stationRepository.findByStationIdAndIsDeletedFalse(request.getStationId())
                .orElseThrow(() -> new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Không tìm thấy trạm."));

        String mode = normalize(request.getMode());
        User manager = MODE_PROMOTE.equals(mode)
                ? promoteExistingUser(request.getExistingUserId())
                : createNewManager(request);

        if (stationManagerRepository.existsByManagerUserIdAndStationStationId(manager.getUserId(), station.getStationId())) {
            throw new UserManagementException(HttpStatus.CONFLICT, "Lỗi: Manager đã được gán cho trạm này.");
        }

        stationManagerRepository.save(StationManager.builder()
                .manager(manager)
                .station(station)
                .build());

        return new UserDTO.ManagerResponse(
                manager.getUserId(),
                manager.getUsername(),
                manager.getFullName(),
                manager.getEmail(),
                manager.getRole(),
                manager.getStatus(),
                station.getStationId(),
                station.getName(),
                MODE_PROMOTE.equals(mode) ? "Nâng quyền Manager thành công." : "Tạo Manager thành công."
        );
    }

    private void validateAdmin(Integer adminId) {
        if (adminId == null) {
            throw new UserManagementException(HttpStatus.FORBIDDEN, "Lỗi: Thiếu adminId.");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserManagementException(HttpStatus.FORBIDDEN, "Lỗi: Không tìm thấy Admin."));

        if (!ROLE_ADMIN.equalsIgnoreCase(admin.getRole())) {
            throw new UserManagementException(HttpStatus.FORBIDDEN, "Lỗi: Bạn không có quyền Admin.");
        }
    }

    private void validateManagerRequest(UserDTO.ManagerRequest request) {
        if (request == null) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu dữ liệu yêu cầu.");
        }
        if (isBlank(request.getMode())) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu mode CREATE hoặc PROMOTE.");
        }
        if (!MODE_CREATE.equals(normalize(request.getMode())) && !MODE_PROMOTE.equals(normalize(request.getMode()))) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: mode chỉ được là CREATE hoặc PROMOTE.");
        }
        if (request.getStationId() == null) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu stationId.");
        }
    }

    private User promoteExistingUser(Integer existingUserId) {
        if (existingUserId == null) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu existingUserId.");
        }

        User user = userRepository.findById(existingUserId)
                .orElseThrow(() -> new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Không tìm thấy người dùng."));

        user.setRole(ROLE_MANAGER);
        user.setStatus(STATUS_ACTIVE);
        return userRepository.save(user);
    }

    private User createNewManager(UserDTO.ManagerRequest request) {
        validateCreateManagerFields(request);
        validateUniqueManagerFields(request);

        User manager = User.builder()
                .username(request.getUsername().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .email(normalizeEmail(request.getEmail()))
                .nationalId(trimToNull(request.getNationalId()))
                .phoneNumber(trimToNull(request.getPhoneNumber()))
                .role(ROLE_MANAGER)
                .status(STATUS_ACTIVE)
                .walletBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return userRepository.save(manager);
    }

    private void validateCreateManagerFields(UserDTO.ManagerRequest request) {
        if (isBlank(request.getUsername())) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu username.");
        }
        if (isBlank(request.getPassword())) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu password.");
        }
        if (isBlank(request.getFullName())) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu fullName.");
        }
        if (isBlank(request.getEmail())) {
            throw new UserManagementException(HttpStatus.BAD_REQUEST, "Lỗi: Thiếu email.");
        }
    }

    private void validateUniqueManagerFields(UserDTO.ManagerRequest request) {
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new UserManagementException(HttpStatus.CONFLICT, "Lỗi: Username đã tồn tại.");
        }
        if (userRepository.existsByEmail(normalizeEmail(request.getEmail()))) {
            throw new UserManagementException(HttpStatus.CONFLICT, "Lỗi: Email đã tồn tại.");
        }
        if (!isBlank(request.getNationalId()) && userRepository.existsByNationalId(request.getNationalId().trim())) {
            throw new UserManagementException(HttpStatus.CONFLICT, "Lỗi: CCCD đã tồn tại.");
        }
        if (!isBlank(request.getPhoneNumber()) && userRepository.existsByPhoneNumber(request.getPhoneNumber().trim())) {
            throw new UserManagementException(HttpStatus.CONFLICT, "Lỗi: Số điện thoại đã tồn tại.");
        }
    }

    private boolean passwordMatches(String rawPassword, String passwordHash) {
        // Hỗ trợ cả SHA-256 (legacy) và BCrypt (mới)
        return passwordHash.equals(hashPassword(rawPassword))
                || passwordEncoder.matches(rawPassword, passwordHash)
                || passwordHash.equals(rawPassword);
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

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
                .status(user.getStatus())
                .walletBalance(user.getWalletBalance())
                .isResident(user.isResident())
                .residentCardId(user.getResidentCardId())
                .build();
    }
}

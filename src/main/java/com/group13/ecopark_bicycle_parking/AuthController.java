package com.group13.ecopark_bicycle_parking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // =========================
    // 1. REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {

        // Kiểm tra username
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
            return loi("Username không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra password
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            return loi("Password không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra full name
        if (newUser.getFullName() == null || newUser.getFullName().trim().isEmpty()) {
            return loi("Full name không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra email
        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
            return loi("Email không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra nationalId
        if (newUser.getNationalId() == null || newUser.getNationalId().trim().isEmpty()) {
            return loi("National ID không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra phone number
        if (newUser.getPhoneNumber() == null || newUser.getPhoneNumber().trim().isEmpty()) {
            return loi("Phone number không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(newUser.getUsername())) {
            return loi("Username đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return loi("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra nationalId đã tồn tại chưa
        if (userRepository.existsByNationalId(newUser.getNationalId())) {
            return loi("National ID đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        // Nếu role chưa có thì gán mặc định là Customer
        if (newUser.getRole() == null || newUser.getRole().trim().isEmpty()) {
            newUser.setRole("Customer");
        }

        // Nếu walletBalance chưa có thì gán mặc định là 0
        if (newUser.getWalletBalance() == null) {
            newUser.setWalletBalance(0L);
        }

        // Lưu user mới vào database
        User userDaLuu = userRepository.save(newUser);

        // Trả về user vừa tạo, nhưng không trả password
        return new ResponseEntity<>(taoUserResponse(userDaLuu), HttpStatus.CREATED);
    }

    // =========================
    // 2. LOGIN
    // Có thể đăng nhập bằng username hoặc email
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        // Kiểm tra ô nhập username/email
        if (loginRequest.getLoginInput() == null || loginRequest.getLoginInput().trim().isEmpty()) {
            return loi("Username hoặc email không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra password
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return loi("Password không được để trống", HttpStatus.BAD_REQUEST);
        }

        // Lấy dữ liệu sau khi bỏ khoảng trắng dư
        String loginInput = loginRequest.getLoginInput().trim();
        String password = loginRequest.getPassword().trim();

        Optional<User> userOptional;

        // Thử đăng nhập bằng username trước
        userOptional = userRepository.findByUsernameAndPassword(loginInput, password);

        // Nếu không thấy thì thử đăng nhập bằng email
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmailAndPassword(loginInput, password);
        }

        // Nếu cả 2 cách đều không được
        if (userOptional.isEmpty()) {
            return loi("Sai username/email hoặc password", HttpStatus.UNAUTHORIZED);
        }

        // Lấy user tìm được
        User user = userOptional.get();

        // Lưu user_id và role vào session để nhớ trạng thái đăng nhập
        session.setAttribute("user_id", user.getUserId());
        session.setAttribute("role", user.getRole());

        // Tạo dữ liệu trả về
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("user", taoUserResponse(user));

        return ResponseEntity.ok(response);
    }

    // =========================
    // 3. LOGOUT
    // =========================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        // Xóa toàn bộ session hiện tại
        session.invalidate();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đăng xuất thành công");

        return ResponseEntity.ok(response);
    }

    // =========================
    // 4. LẤY THÔNG TIN USER ĐANG LOGIN
    // =========================
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {

        // Lấy user_id từ session
        Long userId = (Long) session.getAttribute("user_id");

        // Nếu chưa login thì báo lỗi
        if (userId == null) {
            return loi("Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }

        // Tìm user theo id
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return loi("Không tìm thấy user", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(taoUserResponse(userOptional.get()));
    }

    // =========================
    // 5. CHANGE INFORMATION
    // =========================
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody User thongTinMoi, HttpSession session) {

        // Lấy user_id từ session
        Long userId = (Long) session.getAttribute("user_id");

        // Nếu chưa đăng nhập thì không cho sửa
        if (userId == null) {
            return loi("Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }

        // Tìm user hiện tại trong database
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return loi("Không tìm thấy user", HttpStatus.NOT_FOUND);
        }

        User userHienTai = userOptional.get();

        // Nếu có full name mới thì cập nhật
        if (thongTinMoi.getFullName() != null && !thongTinMoi.getFullName().trim().isEmpty()) {
            userHienTai.setFullName(thongTinMoi.getFullName());
        }

        // Nếu có email mới thì kiểm tra trùng rồi cập nhật
        if (thongTinMoi.getEmail() != null && !thongTinMoi.getEmail().trim().isEmpty()) {
            Optional<User> userTheoEmail = userRepository.findByEmail(thongTinMoi.getEmail());

            // Nếu email này đã thuộc về user khác thì báo lỗi
            if (userTheoEmail.isPresent() && !userTheoEmail.get().getUserId().equals(userHienTai.getUserId())) {
                return loi("Email đã tồn tại", HttpStatus.BAD_REQUEST);
            }

            userHienTai.setEmail(thongTinMoi.getEmail());
        }

        // Nếu có phone number mới thì cập nhật
        if (thongTinMoi.getPhoneNumber() != null && !thongTinMoi.getPhoneNumber().trim().isEmpty()) {
            userHienTai.setPhoneNumber(thongTinMoi.getPhoneNumber());
        }

        // Nếu có address mới thì cập nhật
        if (thongTinMoi.getAddress() != null) {
            userHienTai.setAddress(thongTinMoi.getAddress());
        }

        // Nếu có password mới thì cập nhật
        if (thongTinMoi.getPassword() != null && !thongTinMoi.getPassword().trim().isEmpty()) {
            userHienTai.setPassword(thongTinMoi.getPassword());
        }

        // Lưu lại vào database
        User userDaCapNhat = userRepository.save(userHienTai);

        return ResponseEntity.ok(taoUserResponse(userDaCapNhat));
    }

    // =========================
    // HÀM PHỤ: TRẢ VỀ LỖI
    // =========================
    private ResponseEntity<Map<String, Object>> loi(String message, HttpStatus status) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    // =========================
    // HÀM PHỤ: TẠO RESPONSE USER
    // Không trả password ra ngoài
    // =========================
    private Map<String, Object> taoUserResponse(User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("nationalId", user.getNationalId());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("address", user.getAddress());
        response.put("role", user.getRole());
        response.put("walletBalance", user.getWalletBalance());
        response.put("createdAt", user.getCreatedAt());
        return response;
    }
}
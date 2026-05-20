package com.group13.ecopark_bicycle_parking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Khóa bí mật (Phải dài tối thiểu 256-bit).
    // Lưu ý: Khi làm dự án thực tế, bạn nên đưa chuỗi này vào file application.properties để bảo mật.
    private static final String SECRET_KEY_STRING = "EcoparkBicycleParkingSystemSecretKey2026!@#$";

    // Khởi tạo SecretKey chuẩn cho phiên bản JJWT 0.12.x
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Thời gian sống của Token (VD: 24 giờ)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // ==========================================
    // 1. Hàm tạo Token (Dùng lúc người dùng Đăng nhập thành công ở U1)
    // ==========================================
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // Định danh người dùng (thường là username hoặc userId)
                .issuedAt(new Date()) // Thời điểm cấp phát thẻ
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Thời điểm hết hạn
                .signWith(key) // Ký tên bằng Khóa bí mật
                .compact(); // Đóng gói thành chuỗi String
    }

    // ==========================================
    // 2. Hàm đọc Username từ Token (Dùng ở JwtFilter để biết ai đang gọi API)
    // ==========================================
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key) // Xác thực chữ ký bằng Khóa bí mật
                .build()
                .parseSignedClaims(token) // Phân tích chuỗi Token
                .getPayload() // Lấy phần thân chứa dữ liệu
                .getSubject(); // Lấy ra cái username đã cất vào lúc tạo
    }

    // ==========================================
    // 3. Hàm kiểm tra Token còn hợp lệ không (Chưa hết hạn và đúng chữ ký)
    // ==========================================
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true; // Phân tích thành công nghĩa là Token chuẩn
        } catch (Exception e) {
            System.out.println("Lỗi xác thực Token: " + e.getMessage());
            return false; // Token bị sửa đổi, hết hạn hoặc sai định dạng
        }
    }
}
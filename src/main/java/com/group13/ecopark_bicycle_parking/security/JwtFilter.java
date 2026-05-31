package com.group13.ecopark_bicycle_parking.security;

import com.group13.ecopark_bicycle_parking.user.User;
import com.group13.ecopark_bicycle_parking.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // ĐÃ THÊM: Tiêm UserRepository để tra cứu Role
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy Header "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Kiểm tra xem Header có bắt đầu bằng "Bearer " không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Cắt bỏ 7 chữ "Bearer "
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Token không hợp lệ hoặc đã hết hạn!");
            }
        }

        // 3. Nếu Token chuẩn và chưa có ai đăng nhập trong Context hiện tại
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isTokenValid(token)) {

                // ĐÃ SỬA: Lấy thông tin User từ DB để trích xuất Role
                User user = userRepository.findByUsername(username).orElse(null);

                if (user != null) {
                    // Chuyển đổi Role của User (Ví dụ: "MANAGER") thành Quyền của Spring Security
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

                    // Tạo thẻ thông hành và nạp danh sách Quyền vào (authorities)
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Nạp vào SecurityContext để các bước sau kiểm tra
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 4. Cho phép Request đi tiếp
        filterChain.doFilter(request, response);
    }
}
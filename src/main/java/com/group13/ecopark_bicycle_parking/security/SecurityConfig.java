package com.group13.ecopark_bicycle_parking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt chống giả mạo request (vì ta dùng Token rồi)
                .cors(cors -> cors.configure(http)) // Mở CORS cho Frontend gọi vào
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/apiv1/auth/**").permitAll() // MỞ TOANG cửa cho API Đăng nhập/Đăng ký
                        .anyRequest().authenticated() // CÁC API KHÁC (Thuê xe, Nạp tiền...) BẮT BUỘC PHẢI CÓ TOKEN
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Chế độ không nhớ (Stateless)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Cài trạm kiểm soát JWT lên tuyến đầu

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Khai báo một danh sách User rỗng để chặn Spring Boot tự động tạo mật khẩu mặc định
        return new InMemoryUserDetailsManager();
    }
}
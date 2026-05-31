package com.group13.ecopark_bicycle_parking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Cấu hình CORS sử dụng bean corsConfigurationSource bên dưới
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Tắt CSRF (Bắt buộc để gửi được phương thức POST/PUT từ Frontend)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 3. Cho phép tất cả các request không cần đăng nhập (Vượt rào 403)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            )
            
            // 4. Vô hiệu hóa HTTP Basic và Form Login để tránh hiện popup đăng nhập của trình duyệt
            .httpBasic(Customizer.withDefaults())
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cho phép các nguồn cụ thể (Nên dùng setAllowedOriginPatterns thay vì "*" nếu có credentials)
        configuration.setAllowedOriginPatterns(Collections.singletonList("*")); 
        
        // Cho phép đầy đủ các phương thức
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Cho phép tất cả các Headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Origin", "Accept", "X-Requested-With"));
        
        // Quan trọng: Cho phép gửi Cookie hoặc Token kèm theo
        configuration.setAllowCredentials(true);
        
        // Thời gian cache cấu hình CORS (1 tiếng)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
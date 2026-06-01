package com.group13.ecopark_bicycle_parking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        // 1. MỞ CỬA CHO GIAO DIỆN WEB (Tất cả các file HTML và thư mục con)
                        .requestMatchers("/*.html", "/user-web/**", "/manager-web/**", "/admin-web/**", "/assets/**").permitAll()

                        // 2. MỞ CỬA CHO CÁC API KHÔNG CẦN ĐĂNG NHẬP
                        .requestMatchers("/apiv1/auth/**").permitAll()
                        .requestMatchers("/apiv1/search/**").permitAll()
                        .requestMatchers("/apiv1/statistics/**").permitAll()
                        .requestMatchers("/api/statistics/**").permitAll()

                        // 3. MỞ CỬA CHO CÁC API ADMIN
                        .requestMatchers("/apiv1/admin/**").permitAll()

                        // 4. CHO PHÉP GET danh sách xe (không cần token - AdminPage đọc thống kê)
                        .requestMatchers(HttpMethod.GET, "/apiv1/vehicles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/apiv1/vehicles/**").permitAll()

                        // 5. CÁC API CÒN LẠI BẮT BUỘC PHẢI CÓ TOKEN
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}

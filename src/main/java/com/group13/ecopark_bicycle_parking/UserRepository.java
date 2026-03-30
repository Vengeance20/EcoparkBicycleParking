package com.group13.ecopark_bicycle_parking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Kiểm tra CCCD / nationalId đã tồn tại chưa
    boolean existsByNationalId(String nationalId);

    // Tìm user theo username và password
    Optional<User> findByUsernameAndPassword(String username, String password);

    // Tìm user theo email và password
    Optional<User> findByEmailAndPassword(String email, String password);

    // Tìm user theo email
    Optional<User> findByEmail(String email);
}
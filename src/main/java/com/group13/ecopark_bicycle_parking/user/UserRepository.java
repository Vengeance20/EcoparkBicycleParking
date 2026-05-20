package com.group13.ecopark_bicycle_parking.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByNationalId(String nationalId);
}

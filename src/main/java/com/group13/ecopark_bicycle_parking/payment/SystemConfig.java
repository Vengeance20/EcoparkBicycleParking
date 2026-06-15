package com.group13.ecopark_bicycle_parking.payment;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_configs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String configKey;   // VD: "OPEN_HOUR", "CLOSE_HOUR"

    @Column(nullable = false)
    private String configValue; // VD: "06:00", "22:00"
}
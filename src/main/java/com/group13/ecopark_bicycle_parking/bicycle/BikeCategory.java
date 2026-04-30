package com.group13.ecopark_bicycle_parking.bicycle;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bike_categories")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BikeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal baseFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal extraFee;
}

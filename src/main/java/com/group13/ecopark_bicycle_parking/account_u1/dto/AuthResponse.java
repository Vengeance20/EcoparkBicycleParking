package com.group13.ecopark_bicycle_parking.account.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private String message;
}

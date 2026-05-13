package com.group13.ecopark_bicycle_parking.account.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    @NotBlank
    private String fullName;

    private String phoneNumber;
    private String address;
    private String cardId;
}

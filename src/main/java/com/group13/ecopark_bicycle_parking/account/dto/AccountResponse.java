package com.group13.ecopark_bicycle_parking.account.dto;

import com.group13.ecopark_bicycle_parking.account.model.Account;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String cardId;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse fromAccount(Account a) {
        return AccountResponse.builder()
                .id(a.getId()).email(a.getEmail()).fullName(a.getFullName())
                .phoneNumber(a.getPhoneNumber()).address(a.getAddress())
                .cardId(a.getCardId()).role(a.getRole().name())
                .createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt())
                .build();
    }
}

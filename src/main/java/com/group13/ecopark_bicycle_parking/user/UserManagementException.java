package com.group13.ecopark_bicycle_parking.user;

import org.springframework.http.HttpStatus;

public class UserManagementException extends RuntimeException {

    private final HttpStatus status;

    public UserManagementException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

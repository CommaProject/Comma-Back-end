package com.team.comma.global.jwt.exception;

public class RequireRefreshToken extends RuntimeException {
    public RequireRefreshToken(String message) {
        super(message);
    }
}

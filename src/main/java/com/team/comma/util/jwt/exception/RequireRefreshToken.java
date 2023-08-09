package com.team.comma.util.jwt.exception;

public class RequireRefreshToken extends RuntimeException {
    public RequireRefreshToken(String message) {
        super(message);
    }
}

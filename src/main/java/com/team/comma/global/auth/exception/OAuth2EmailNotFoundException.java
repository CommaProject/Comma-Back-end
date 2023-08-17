package com.team.comma.global.auth.exception;

public class OAuth2EmailNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OAuth2EmailNotFoundException(String message) {
        super(message);
    }
}

package com.team.comma.spotify.search.exception;

public class ExpirationTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExpirationTokenException(String message) {
        super(message);
    }
}

package com.team.comma.spotify.recommend.exception;

public class RecommendException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RecommendException(String message) {
        super(message);
    }
}
package com.team.comma.domain.spotify.playlist.exception;

public class PlaylistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PlaylistException(String message) {
        super(message);
    }
}

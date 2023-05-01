package com.team.comma.spotify.playlist.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlaylistErrorResult {

    PLAYLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "Playlist Not Found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

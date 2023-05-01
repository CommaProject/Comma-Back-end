package com.team.comma.spotify.playlist.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaylistException extends RuntimeException{

    private final PlaylistErrorResult errorResult;
}

package com.team.comma.domain.playlist.archive.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ArchiveResponse {

    private final long archiveId;
    private final String comment;

    private final long playlistId;
    private final String playlist;
}

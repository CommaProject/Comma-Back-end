package com.team.comma.domain.playlist.archive.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ArchiveResponse {

    private final long archiveId;
    private final String comment;
    private final String createdDate;

    private final long playlistId;
    private final long playlistTitle;
    private final String albumImageUrl;
}

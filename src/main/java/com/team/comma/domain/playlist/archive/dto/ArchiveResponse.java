package com.team.comma.domain.playlist.archive.dto;

import com.team.comma.domain.playlist.archive.domain.Archive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public final class ArchiveResponse {

    private final long archiveId;
    private final String comment;
    private final LocalDateTime createDate;
    private final boolean publicFlag;

    private final long playlistId;
    private final String playlistTitle;
    private final String albumImageUrl;

    private ArchiveResponse(Archive archive) {
        this.archiveId = archive.getId();
        this.comment = archive.getComment();
        this.createDate = archive.getCreateDate();
        this.publicFlag = archive.getPublicFlag();
        this.playlistId = archive.getPlaylist().getId();
        this.playlistTitle = archive.getPlaylist().getPlaylistTitle();
        this.albumImageUrl = archive.getPlaylist().getPlaylistTrackList().get(0).getTrack().getAlbumImageUrl();
    }

    public static ArchiveResponse of(Archive archive) {
        return new ArchiveResponse(archive);
    }
}

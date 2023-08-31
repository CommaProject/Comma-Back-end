package com.team.comma.domain.playlist.playlist.dto;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public final class PlaylistResponse {

    private final long playlistId;
    private final String playlistTitle;
    private final boolean alarmFlag;
    private final LocalTime alarmStartTime;
    private final int trackCount;
    private final String repAlbumImageUrl;

    private PlaylistResponse(Playlist playlist, String repAlbumImageUrl) {
        this.playlistId = playlist.getId();
        this.playlistTitle = playlist.getPlaylistTitle();
        this.alarmFlag = playlist.getAlarmFlag();
        this.alarmStartTime = playlist.getAlarmStartTime();
        this.trackCount = playlist.getPlaylistTrackList().size();
        this.repAlbumImageUrl = repAlbumImageUrl;
    }

    public static PlaylistResponse of(Playlist playlist, String repAlbumImageUrl) {
        return new PlaylistResponse(playlist, repAlbumImageUrl);
    }

}

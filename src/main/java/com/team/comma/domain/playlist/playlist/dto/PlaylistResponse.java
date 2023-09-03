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
    private final long totalDurationTime;

    private PlaylistResponse(Playlist playlist, String repAlbumImageUrl, long totalDurationTime) {
        this.playlistId = playlist.getId();
        this.playlistTitle = playlist.getPlaylistTitle();
        this.alarmFlag = playlist.getAlarmFlag();
        this.alarmStartTime = playlist.getAlarmStartTime();
        this.trackCount = playlist.getPlaylistTrackList().size();
        this.repAlbumImageUrl = repAlbumImageUrl;
        this.totalDurationTime = totalDurationTime;
    }

    public static PlaylistResponse of(Playlist playlist, String repAlbumImageUrl, long totalDurationTime) {
        return new PlaylistResponse(playlist, repAlbumImageUrl, totalDurationTime);
    }

}

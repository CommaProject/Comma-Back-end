package com.team.comma.domain.playlist.playlist.dto;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.track.domain.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
@Builder
public final class PlaylistResponse {

    private final long playlistId;
    private final String playlistTitle;
    private final boolean alarmFlag;
    private final LocalTime alarmStartTime;
    private final int trackCount;
    private final String repAlbumImageUrl;
    private final long totalDurationTime;

    public static PlaylistResponse of(Playlist playlist) {
        long totalDurationTime = 0;
        for(PlaylistTrack playlistTrack : playlist.getPlaylistTrackList()){
            totalDurationTime += playlistTrack.getTrack().getDurationTimeMs();
        }
        return PlaylistResponse.builder()
                .playlistId(playlist.getId())
                .playlistTitle(playlist.getPlaylistTitle())
                .alarmFlag(playlist.getAlarmFlag())
                .alarmStartTime(playlist.getAlarmStartTime())
                .trackCount(playlist.getPlaylistTrackList().size())
                .repAlbumImageUrl(playlist.getPlaylistTrackList().get(0).getTrack().getAlbumImageUrl())
                .totalDurationTime(totalDurationTime)
                .build();
    }

}

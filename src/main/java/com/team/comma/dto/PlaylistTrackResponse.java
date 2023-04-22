package com.team.comma.dto;

import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.TrackArtist;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackResponse {
    private final Long id;
    private final String trackTitle;
    private final Integer durationTimeMs;
    private final String albumImageUrl;
    private final Boolean trackAlarmFlag;

    private final List<TrackArtist> trackArtistList;

    private PlaylistTrackResponse(PlaylistTrack playlistTrack, Track track) {
        this.id = track.getId();
        this.trackTitle = track.getTrackTitle();
        this.durationTimeMs = track.getDurationTimeMs();
        this.albumImageUrl = track.getAlbumImageUrl();
        this.trackAlarmFlag = playlistTrack.getTrackAlarmFlag();
        this.trackArtistList = new ArrayList<>(track.getTrackArtistList());
    }

    public static PlaylistTrackResponse of(PlaylistTrack playlistTrack, Track track) {
        return new PlaylistTrackResponse(playlistTrack, track);
    }
}

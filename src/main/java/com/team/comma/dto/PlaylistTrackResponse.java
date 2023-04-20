package com.team.comma.dto;

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
    private final Integer durationMs;
    private final String albumImageUrl;
    private final Boolean alarmFlag;

    private final List<TrackArtist> artistName;

    private PlaylistTrackResponse(Track track, List<TrackArtist> trackArtist) {
        this.id = track.getId();
        this.trackTitle = track.getTrackTitle();
        this.durationMs = track.getDurationTimeMs();
        this.albumImageUrl = track.getAlbumImageUrl();
        this.alarmFlag = track.isAlarmFlag();
        this.artistName = new ArrayList<>(trackArtist);
    }

    public static PlaylistTrackResponse of(Track track, List<TrackArtist> trackArtist) {
        return new PlaylistTrackResponse(track, trackArtist);
    }
}

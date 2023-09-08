package com.team.comma.domain.playlist.track.dto;

import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class PlaylistTrackResponse {
    private final boolean trackAlarmFlag;

    private final List<TrackArtistResponse> trackArtistList;

    public PlaylistTrackResponse(boolean trackAlarmFlag, List<TrackArtistResponse> trackArtistList) {
        this.trackAlarmFlag = trackAlarmFlag;
        this.trackArtistList = new ArrayList<>(trackArtistList);
    }

    public static PlaylistTrackResponse of(boolean trackAlarmFlag, List<TrackArtistResponse> trackArtistList) {
        return new PlaylistTrackResponse(trackAlarmFlag, trackArtistList);
    }

    public List<TrackArtistResponse> getTrackArtistList() {
        return Collections.unmodifiableList(trackArtistList);
    }

}

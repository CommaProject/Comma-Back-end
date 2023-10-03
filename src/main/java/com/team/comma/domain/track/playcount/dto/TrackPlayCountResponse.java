package com.team.comma.domain.track.playcount.dto;

import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackPlayCountResponse {

    private Long playCount;
    private TrackArtistResponse trackArtist;

    public static TrackPlayCountResponse of(long playCount, TrackArtistResponse trackArtistResponse) {
        return TrackPlayCountResponse.builder()
                .playCount(playCount)
                .trackArtist(trackArtistResponse)
                .build();
    }
}

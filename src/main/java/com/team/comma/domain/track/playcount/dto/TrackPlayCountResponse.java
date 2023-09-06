package com.team.comma.domain.track.playcount.dto;

import com.team.comma.domain.track.track.domain.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackPlayCountResponse {
    private Integer playCount;

    private Track track;
}

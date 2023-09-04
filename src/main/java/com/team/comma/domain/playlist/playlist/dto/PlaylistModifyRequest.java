package com.team.comma.domain.playlist.playlist.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistModifyRequest {

    private long playlistId;
    private String playlistTitle;
    private LocalTime alarmStartTime;
    private List<Integer> alarmDays;

}

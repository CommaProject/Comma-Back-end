package com.team.comma.domain.alert.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertResponse {
    private long userId;
    private long playlistId;
    private String playlistTitle;
    private boolean alarmFlag;
    private LocalTime alarmStartTime;
    private int trackCount;
    private String repAlbumImageUrl;
    private long totalDurationTime;
}

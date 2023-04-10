package com.team.comma.dto;

import com.team.comma.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PlaylistResponse {

    private final Long playlistKey;
    private final UserEntity userEntity;
    private final String alarmYn;
    private final String alarmSetDay;
    private final String alarmStartTime;
    private final String alarmEndTime;

}
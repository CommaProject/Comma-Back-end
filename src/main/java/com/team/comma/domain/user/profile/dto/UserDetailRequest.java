package com.team.comma.domain.user.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDetailRequest {

    private String nickName;
    private List<String> artistNames;
}

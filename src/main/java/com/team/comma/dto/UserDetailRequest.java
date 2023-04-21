package com.team.comma.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailRequest {
    private String nickName;
    private String sex;
    private String age;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime recommendTime;

    private List<String> genres;
    private List<String> artistNames;
}

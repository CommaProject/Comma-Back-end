package com.team.comma.spotify.recommend.dto;

import com.team.comma.spotify.recommend.constant.RecommendType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequest {

    private long recommendPlaylistId;
    private RecommendType recommendType;
    private String recommendToEmail;
    private String comment;

}

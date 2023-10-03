package com.team.comma.domain.playlist.recommend.dto;

import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.user.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequest {

    private RecommendType recommendType;
    private long playlistId;
    private String comment;
    private String toUserEmail;

    public static RecommendRequest of(Recommend recommend) {
        return RecommendRequest.builder()
                .playlistId(recommend.getPlaylist().getId())
                .recommendType(recommend.getRecommendType())
                .toUserEmail(recommend.getToUser().getEmail())
                .comment(recommend.getComment())
                .build();
    }

}

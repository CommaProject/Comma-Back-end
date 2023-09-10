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

    private long recommendPlaylistId;
    private RecommendType recommendType;
    private String recommendToEmail;
    private String comment;

    public Recommend toRecommendEntity(User toUser, Playlist playlist){
        return Recommend.builder()
                .toUser(toUser)
                .recommendType(recommendType)
                .comment(comment)
                .playlist(playlist)
                .playCount(0L)
                .build();
    }

}

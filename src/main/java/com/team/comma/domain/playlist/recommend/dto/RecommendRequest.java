package com.team.comma.domain.playlist.recommend.dto;

import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
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

    public Recommend toRecommendEntity(User fromUser, User toUser, Playlist playlist){
        return Recommend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .recommendType(recommendType)
                .comment(comment)
                .playlist(playlist)
                .playCount(0L)
                .build();
    }

    public Recommend toRecommendEntity(User fromUser, Playlist playlist){
        return Recommend.builder()
                .fromUser(fromUser)
                .recommendType(recommendType)
                .comment(comment)
                .playlist(playlist)
                .playCount(0L)
                .build();
    }
}

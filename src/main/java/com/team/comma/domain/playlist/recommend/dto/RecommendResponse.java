package com.team.comma.domain.playlist.recommend.dto;

import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.user.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class RecommendResponse {

    private final long recommendId;
    private final String comment;

    private final String userNickname;
    private final String userProfileImage;

    private final long playlistId;
    private final String playlistTitle;
    private final String repAlbumImageUrl;

    private final long trackCount;
    private final long playCount;

    public static RecommendResponse of(Recommend recommend, User user) {
        return RecommendResponse.builder()
                .recommendId(recommend.getId())
                .comment(recommend.getComment())
                .userNickname(user.getUserDetail().getNickname())
                .userProfileImage(user.getUserDetail().getProfileImageUrl())
                .playlistId(recommend.getPlaylist().getId())
                .playlistTitle(recommend.getPlaylist().getPlaylistTitle())
                .repAlbumImageUrl(recommend.getPlaylist().getPlaylistTrackList().get(0).getTrack().getAlbumImageUrl())
                .trackCount(recommend.getPlaylist().getPlaylistTrackList().size())
                .playCount(recommend.getPlayCount())
                .build();
    }

}

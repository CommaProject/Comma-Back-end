package com.team.comma.domain.playlist.dto.recommend;

import com.team.comma.domain.playlist.domain.Recommend;
import com.team.comma.domain.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

    private RecommendResponse(Recommend recommend, User user, long trackCount) {
        this.recommendId = recommend.getId();
        this.comment = recommend.getComment();

        this.userNickname = user.getUserDetail().getNickname();
        this.userProfileImage = user.getUserDetail().getProfileImageUrl();

        this.playlistId = recommend.getPlaylist().getId();
        this.playlistTitle = recommend.getPlaylist().getPlaylistTitle();
        this.repAlbumImageUrl = recommend.getPlaylist().getPlaylistTrackList().get(0).getTrack().getAlbumImageUrl();

        this.trackCount = trackCount;
        this.playCount = recommend.getPlayCount();
    }

    public static RecommendResponse of(Recommend recommend, User user, long trackCount) {
        return new RecommendResponse(recommend, user, trackCount);
    }

}

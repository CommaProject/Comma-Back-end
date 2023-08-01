package com.team.comma.spotify.recommend.dto;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.recommend.domain.Recommend;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecommendResponse {

    private final long recommendId;
    private final String fromUserNickname;
    private final String fromUserProfileImage;
    private final String toUserNickname;
    private final String toUserProfileImage;
    private final String comment;

    private final long playlistId;
    private final String playlistTitle;
    private final String repAlbumImageUrl;

    private final long trackCount;

    private final long playCount;

    private RecommendResponse(Recommend recommend, long trackCount) {
        this.recommendId = recommend.getId();
        this.fromUserNickname = recommend.getFromUser().getUserDetail().getNickname();
        this.fromUserProfileImage = recommend.getFromUser().getUserDetail().getProfileImageUrl();
        this.toUserNickname = recommend.getToUser().getUserDetail().getNickname();
        this.toUserProfileImage = recommend.getToUser().getUserDetail().getProfileImageUrl();
        this.comment = recommend.getComment();
        this.playlistId = recommend.getPlaylist().getId();
        this.playlistTitle = recommend.getPlaylist().getPlaylistTitle();
        this.repAlbumImageUrl = recommend.getPlaylist().getPlaylistTrackList().get(0).getTrack().getAlbumImageUrl();
        this.trackCount = trackCount;
        this.playCount = recommend.getPlayCount();
    }

    public static RecommendResponse of(Recommend recommend, long trackCount) {
        return new RecommendResponse(recommend, trackCount);
    }

}

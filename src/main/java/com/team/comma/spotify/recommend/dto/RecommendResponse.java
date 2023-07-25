package com.team.comma.spotify.recommend.dto;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.recommend.domain.Recommend;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecommendResponse {

    private final long recommendId;
    private final String fromUserEmail;
    private final long playlistId;
    private final String playlistTitle;
    private final String repAlbumImageUrl;
    private final long trackCount;

    private RecommendResponse(Recommend recommend, long trackCount) {
        this.recommendId = recommend.getId();
        this.fromUserEmail = recommend.getFromUser().getEmail();
        this.playlistId = recommend.getPlaylist().getId();
        this.playlistTitle = recommend.getPlaylist().getPlaylistTitle();
        this.repAlbumImageUrl = recommend.getPlaylist().getPlaylistTrackList().get(0).getTrack().getAlbumImageUrl();
        this.trackCount = trackCount;
    }

    public static RecommendResponse of(Recommend recommend, long trackCount) {
        return new RecommendResponse(recommend, trackCount);
    }

}
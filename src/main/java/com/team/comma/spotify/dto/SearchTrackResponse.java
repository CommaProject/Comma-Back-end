package com.team.comma.spotify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Schema(description = "Spotify 트랙 탐색")
public class SearchTrackResponse {


    private String trackId;
    private String trackName;
    private String artist;
    private String artistId;
    private String albumId;
    private String previewUrl;
    private Image[] images;
    private String releaseData;

    private String durationMinute;
    private String durationSecond;

    public static String MillToMinute(int ms) {
        SimpleDateFormat format = new SimpleDateFormat("mm");
        String date = format.format(new Timestamp(ms));

        return date;
    }

    public static String MillToSecond(int ms) {
        SimpleDateFormat format = new SimpleDateFormat("ss");
        String date = format.format(new Timestamp(ms));

        return date;
    }

    public static SearchTrackResponse createTrackResponse(Track track) {
        return SearchTrackResponse.builder()
                .trackId(track.getId())
                .images(track.getAlbum().getImages())
                .trackName(track.getName())
                .artist(track.getArtists()[0].getName())
                .artistId(track.getArtists()[0].getId())
                .albumId(track.getAlbum().getId())
                .previewUrl(track.getPreviewUrl())
                .releaseData(track.getAlbum().getReleaseDate())
                .durationMinute(MillToMinute(track.getDurationMs()))
                .durationSecond(MillToSecond(track.getDurationMs()))
                .build();
    }

    public static SearchTrackResponse createTrackResponse(TrackSimplified track, AlbumSimplified album) {
        return SearchTrackResponse.builder()
                .trackId(track.getId())
                .images(album.getImages())
                .trackName(track.getName())
                .artist(track.getArtists()[0].getName())
                .artistId(track.getArtists()[0].getId())
                .albumId(album.getId())
                .previewUrl(track.getPreviewUrl())
                .releaseData(album.getReleaseDate())
                .durationMinute(MillToMinute(track.getDurationMs()))
                .durationSecond(MillToSecond(track.getDurationMs()))
                .build();
    }

}

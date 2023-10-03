package com.team.comma.domain.track.track.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.service.ArtistService;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.spotify.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.List;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final ArtistService artistService;
    private final SearchService searchService;

    public MessageResponse findTrackByMostFavorite() {
        List<TrackArtistResponse> result = trackRepository.findTrackMostRecommended();

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public Track findTrackOrSave(final String spotifyTrackId) {
        return trackRepository.findBySpotifyTrackId(spotifyTrackId)
                .orElseGet(() -> saveTrack(spotifyTrackId));
    }

    public Track saveTrack(final String spotifyTrackId) {
        se.michaelthelin.spotify.model_objects.specification
                .Track spotifyTrack = searchService.getTrackByTrackId(spotifyTrackId);

        Track track = Track.createTrack(spotifyTrack);
        for(ArtistSimplified artistSimplified : spotifyTrack.getArtists()){
            Artist artist = artistService.findArtistOrSave(artistSimplified.getId());
            track.addTrackArtistList(artist);
        }

        return trackRepository.save(track);
    }

}

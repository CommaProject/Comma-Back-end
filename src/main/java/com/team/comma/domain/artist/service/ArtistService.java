package com.team.comma.domain.artist.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.spotify.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final SearchService searchService;

    public Artist findArtistOrSave(final String spotifyArtistId) {
        return artistRepository.findArtistBySpotifyArtistId(spotifyArtistId)
                .orElseGet(() -> saveArtist(spotifyArtistId));
    }

    public Artist saveArtist(final String spotifyArtistId) {
        return artistRepository.save(
                Artist.createArtistWithSpotifyArtist(searchService.getArtistByArtistId(spotifyArtistId)));
    }

}

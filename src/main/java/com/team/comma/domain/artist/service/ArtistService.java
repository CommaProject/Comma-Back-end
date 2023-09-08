package com.team.comma.domain.artist.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public Artist findArtistOrSave(String artistId , String artistName) {
        return artistRepository.findArtistBySpotifyArtistId(artistId)
                .orElseGet(() -> artistRepository.save(Artist.createArtist(artistId , artistName)));
    }

}

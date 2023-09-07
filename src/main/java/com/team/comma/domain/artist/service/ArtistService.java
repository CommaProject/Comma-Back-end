package com.team.comma.domain.artist.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.team.comma.domain.artist.domain.Artist.createArtist;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public Artist findArtistOrSave(String artistId , String artistName) {
        return artistRepository.findArtistByArtistId(artistId)
                .orElseGet(() -> createArtist(artistId , artistName));
    }

}

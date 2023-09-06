package com.team.comma.domain.track.track.service;

import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.spotify.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final JwtTokenProvider jwtTokenProvider;
    private final FavoriteTrackRepository favoriteTrackRepository;
    private final TrackRepository trackRepository;
    private final SearchService searchService;

    public MessageResponse findTrackByFavoriteTrack(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        List<Track> result = favoriteTrackRepository.findFavoriteTrackByEmail(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse findTrackByMostFavorite() {
        List<Track> result = trackRepository.findTrackMostRecommended();

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public Track findTrackOrSave(final String spotifyTrackId) {
        return trackRepository.findBySpotifyTrackId(spotifyTrackId)
                .orElseGet(() -> saveTrack(spotifyTrackId));
    }

    public Track saveTrack(final String spotifyTrackId) {
        return trackRepository.save(searchService.searchTrackByTrackId(spotifyTrackId));
    }

}

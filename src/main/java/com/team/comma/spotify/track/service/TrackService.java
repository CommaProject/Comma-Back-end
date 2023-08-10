package com.team.comma.spotify.track.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.search.service.SearchService;
import com.team.comma.spotify.track.domain.FavoriteTrack;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackPlayCount;
import com.team.comma.spotify.track.dto.TrackPlayCountResponse;
import com.team.comma.spotify.track.dto.TrackRequest;
import com.team.comma.spotify.track.exception.TrackException;
import com.team.comma.spotify.track.repository.count.TrackPlayCountRepository;
import com.team.comma.spotify.track.repository.favorite.FavoriteTrackRepository;
import com.team.comma.spotify.track.repository.track.TrackRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;
import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.team.comma.spotify.track.domain.FavoriteTrack.createFavoriteTrack;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TrackPlayCountRepository trackPlayCountRepository;
    private final FavoriteTrackRepository favoriteTrackRepository;
    private final UserRepository userRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final SearchService searchService;

    @Transactional
    public MessageResponse countPlayCount(String accessToken , String trackId) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        TrackPlayCount trackPlayCount = trackPlayCountRepository.findTrackPlayCountByUserEmail(userEmail , trackId)
                .orElseThrow(() -> new TrackException("트랙을 찾을 수 없습니다."));

        trackPlayCount.updatePlayCount();

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findMostListenedTrack(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByMostListenedTrack(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse findMostListenedTrackByFriend(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByFriend(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS, result);
    }

    public MessageResponse findTrackByFavoriteTrack(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        List<Track> result = favoriteTrackRepository.findFavoriteTrackByEmail(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse findTrackByMostFavorite() {
        List<Track> result = trackRepository.findTrackMostRecommended();

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    @Transactional
    public MessageResponse addFavoriteTrack(String accessToken , TrackRequest trackRequest) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자 정보를 찾을 수 없습니다."));

        FavoriteTrack result = createFavoriteTrack(user , findTrackOrElseSave(trackRequest));
        favoriteTrackRepository.save(result);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public Track findTrackOrElseSave(TrackRequest trackRequest) {
        return trackRepository.findBySpotifyTrackId(trackRequest.getSpotifyTrackId())
                .orElseGet(() -> saveNewTrack(trackRequest.getSpotifyTrackId()));
    }

    public Track saveNewTrack(String trackId) {
        return trackRepository.save(searchService.searchTrackByTrackId(trackId));
    }

    public MessageResponse updateAlarmFlag(Long trackId) {
        trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException());

        long updatedCount = playlistTrackRepository.changeAlarmFlagWithTrackId(trackId);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}

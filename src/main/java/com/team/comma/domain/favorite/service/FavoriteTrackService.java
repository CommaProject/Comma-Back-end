package com.team.comma.domain.favorite.service;

import com.team.comma.domain.favorite.domain.FavoriteTrack;
import com.team.comma.domain.favorite.dto.FavoriteTrackResponse;
import com.team.comma.domain.favorite.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.dto.TrackRequest;
import com.team.comma.domain.track.service.TrackService;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.user.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;

import java.util.ArrayList;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class FavoriteTrackService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TrackService trackService;
    private final FavoriteTrackRepository favoriteTrackRepository;

    @Transactional
    public MessageResponse createFavoriteTrack(String accessToken , TrackRequest trackRequest) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자 정보를 찾을 수 없습니다."));

        FavoriteTrack result = FavoriteTrack.createFavoriteTrack(user , trackService.findTrackOrElseSave(trackRequest.getSpotifyTrackId()));
        favoriteTrackRepository.save(result);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findAllFavoriteTrack(final String accessToken) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자 정보를 찾을 수 없습니다."));

        List<FavoriteTrack> favoriteTracks = findAllByUser(user);

        List<FavoriteTrackResponse> favoriteTrackResponses = new ArrayList<>();
        for(FavoriteTrack favoriteTrack : favoriteTracks) {
            favoriteTrackResponses.add(FavoriteTrackResponse.of(favoriteTrack));
        }

        return MessageResponse.of(REQUEST_SUCCESS, favoriteTrackResponses);
    }

    public List<FavoriteTrack> findAllByUser(final User user) {
        return favoriteTrackRepository.findAllByUser(user);
    }

}

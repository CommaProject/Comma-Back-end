package com.team.comma.domain.favorite.track.service;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackRequest;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class FavoriteTrackService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TrackService trackService;
    private final FavoriteTrackRepository favoriteTrackRepository;

    @Transactional
    public MessageResponse createFavoriteTrack(String accessToken , FavoriteTrackRequest favoriteTrackRequest) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        FavoriteTrack result = FavoriteTrack.buildFavoriteTrack(user , trackService.findTrackOrSave(favoriteTrackRequest.getSpotifyTrackId()));
        favoriteTrackRepository.save(result);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findAllFavoriteTrack(final String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        List<FavoriteTrackResponse> favoriteTrackResponses = findAllFavoriteTrackByUser(user);

        return MessageResponse.of(REQUEST_SUCCESS, favoriteTrackResponses);
    }

    public List<FavoriteTrackResponse> findAllFavoriteTrackByUser(final User user) {
        return favoriteTrackRepository.findAllFavoriteTrackByUser(user);
    }

}

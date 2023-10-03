package com.team.comma.domain.favorite.track.service;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackRequest;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class FavoriteTrackService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TrackService trackService;
    private final FavoriteTrackRepository favoriteTrackRepository;

    @Transactional
    public MessageResponse createFavoriteTrack(String accessToken , FavoriteTrackRequest favoriteTrackRequest) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userService.findUserOrThrow(userEmail);

        FavoriteTrack result = FavoriteTrack.createFavoriteTrack(user , trackService.findTrackOrSave(favoriteTrackRequest.getSpotifyTrackId()));
        favoriteTrackRepository.save(result);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findAllFavoriteTrack(final String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userService.findUserOrThrow(userEmail);

        List<FavoriteTrackResponse> favoriteTrackResponses = findAllFavoriteTrackByUser(user);

        return MessageResponse.of(REQUEST_SUCCESS, favoriteTrackResponses);
    }

    public List<FavoriteTrackResponse> findAllFavoriteTrackByUser(final User user) {
        return favoriteTrackRepository.findAllFavoriteTrackByUser(user);
    }

    public FavoriteTrack findFavoriteTrackOrThrow(final long favoriteTrackId) {
        return favoriteTrackRepository.findById(favoriteTrackId)
                .orElseThrow(() -> new EntityNotFoundException("좋아요 표시 한 트랙을 찾을 수 없습니다."));
    }

    @Transactional
    public MessageResponse deleteFavoriteTrack(final String accessToken, final long favoriteTrackId) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userService.findUserOrThrow(userEmail);
        FavoriteTrack favoriteTrack = findFavoriteTrackOrThrow(favoriteTrackId);

        favoriteTrackRepository.delete(favoriteTrack);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}

package com.team.comma.domain.favorite.artist.service;

import com.team.comma.domain.favorite.artist.dto.FavoriteArtistResponse;
import com.team.comma.domain.favorite.artist.exception.FavoriteArtistException;
import com.team.comma.domain.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class FavoriteArtistService {

    private final FavoriteArtistRepository favoriteArtistRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public MessageResponse isFavoriteArtist(String token , String artistName) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));
        boolean isAddedArtist = false;

        if(isAddedFavoriteArtist(user , artistName)) {
            isAddedArtist = true;
        }

        return MessageResponse.of(REQUEST_SUCCESS , isAddedArtist);
    }

    public boolean isAddedFavoriteArtist(User user , String artistName) {
        if(favoriteArtistRepository.findFavoriteArtistByUser(user , artistName).isPresent()) {
            return true;
        };
        return false;
    }

    @Transactional
    public MessageResponse createFavoriteArtist(String token , String artistName) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        if(isAddedFavoriteArtist(user , artistName)) {
            throw new FavoriteArtistException("이미 추가된 관심 아티스트입니다.");
        }

        user.addFavoriteArtist(artistName);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse deleteFavoriteArtist(String token , String artistName) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        favoriteArtistRepository.deleteByUser(user , artistName);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findAllFavoriteArtist(final String accessToken) throws AccountException{
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        List<FavoriteArtistResponse> favoriteArtistResponses = findAllFavoriteArtistByUser(user);

        return MessageResponse.of(REQUEST_SUCCESS, favoriteArtistResponses);
    }

    public List<FavoriteArtistResponse> findAllFavoriteArtistByUser(final User user) {
        return favoriteArtistRepository.findAllFavoriteArtistByUser(user);
    }

}

package com.team.comma.domain.playlist.recommend.service;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.recommend.constant.RecommendListType;
import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendListRequest;
import com.team.comma.domain.playlist.recommend.dto.RecommendRequest;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.playlist.recommend.exception.RecommendException;
import com.team.comma.domain.playlist.recommend.repository.RecommendRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;

    private final PlaylistRepository playlistRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MessageResponse addRecommend(final String accessToken, final RecommendRequest recommendRequest)
            throws AccountException {
        final User fromUser = findUserByToken(accessToken);

        final Playlist playlist = playlistRepository.findById(recommendRequest.getRecommendPlaylistId())
                .orElseThrow(()-> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        Recommend buildEntity;
        if(recommendRequest.getRecommendType().equals(RecommendType.FOLLOWING)){
            User toUser = userRepository.findUserByEmail(recommendRequest.getRecommendToEmail())
                    .orElseThrow(() -> new AccountException("추천 받는 사용자 정보가 올바르지 않습니다."));

            buildEntity = recommendRequest.toRecommendEntity(toUser, playlist);
            getRecommendCountByToUserAndPlaylist(buildEntity);
        } else {
            buildEntity = recommendRequest.toRecommendEntity(fromUser, playlist);
        }

        playlistRepository.updateRecommendCountByPlaylistId(playlist.getId());

        recommendRepository.save(buildEntity);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public long getRecommendCountByToUserAndPlaylist(final Recommend recommend) {
        final long isRecommended = recommendRepository.getRecommendCountByToUserAndPlaylist(recommend);
        if(isRecommended > 0){
            throw new RecommendException("사용자에게 이미 추천한 플레이리스트 입니다.");
        }
        return isRecommended;
    }

    public MessageResponse getRecommendList(final String accessToken, final RecommendListRequest recommendListRequest) throws AccountException {
        final User user = findUserByToken(accessToken);

        if(recommendListRequest.getRecommendListType().equals(RecommendListType.RECIEVED)){
            return getRecommendsByToUser(user);
        } else if(recommendListRequest.getRecommendListType().equals(RecommendListType.SENDED)){
            return getRecommendsByFromUser(user);
        } else {
            return getRecommendsToAnonymous();
        }
    }

    public User findUserByToken(final String accessToken) throws AccountException {
        final String userName = jwtTokenProvider.getUserPk(accessToken);
        final User user = userRepository.findUserByEmail(userName)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));
        return user;
    }

    public MessageResponse getRecommendsByToUser(final User user){
        return MessageResponse.of(REQUEST_SUCCESS, recommendRepository.getRecommendsByToUser(user));
    }

    public MessageResponse getRecommendsByFromUser(final User user){
        return MessageResponse.of(REQUEST_SUCCESS, recommendRepository.getRecommendsByFromUser(user));
    }

    public MessageResponse getRecommendsToAnonymous(){
        return MessageResponse.of(REQUEST_SUCCESS, recommendRepository.getRecommendsToAnonymous());
    }

    @Transactional
    public MessageResponse getRecommend(final long recommendId) {
        final Recommend recommend = recommendRepository.findById(recommendId)
                .orElseThrow(() -> new RecommendException("추천 정보를 찾을 수 없습니다."));

        recommendRepository.increasePlayCount(recommendId);

        return MessageResponse.of(REQUEST_SUCCESS, RecommendResponse.of(recommend, recommend.getToUser(), 0));
    }

}

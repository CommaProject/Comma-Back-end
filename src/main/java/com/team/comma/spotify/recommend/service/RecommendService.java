package com.team.comma.spotify.recommend.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.recommend.constant.RecommendListType;
import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.recommend.dto.RecommendListRequest;
import com.team.comma.spotify.recommend.dto.RecommendResponse;
import com.team.comma.spotify.recommend.exception.RecommendException;
import com.team.comma.spotify.recommend.repository.RecommendRepository;
import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.spotify.recommend.dto.RecommendRequest;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

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
        final String userName = jwtTokenProvider.getUserPk(accessToken);
        final User fromUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("추천 보낸 사용자 정보가 올바르지 않습니다."));

        final Playlist playlist = playlistRepository.findById(recommendRequest.getRecommendPlaylistId())
                .orElseThrow(()-> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        Recommend buildEntity;
        if(recommendRequest.getRecommendType().equals(RecommendType.FOLLOWING)){
            User toUser = userRepository.findByEmail(recommendRequest.getRecommendToEmail())
                    .orElseThrow(() -> new AccountException("추천 받는 사용자 정보가 올바르지 않습니다."));

            buildEntity = recommendRequest.toRecommendEntity(fromUser, toUser, playlist);
        } else {
            buildEntity = recommendRequest.toRecommendEntity(fromUser, playlist);
        }

        final long isRecommended = recommendRepository.getRecommendCountByToUserAndPlaylist(buildEntity);
        if(isRecommended > 0){
            throw new RecommendException("사용자에게 이미 추천한 플레이리스트 입니다.");
        }

        recommendRepository.save(buildEntity);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse getRecommendList(final String accessToken, final RecommendListRequest recommendListRequest) throws AccountException {
        final String userName = jwtTokenProvider.getUserPk(accessToken);
        final User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("사용자 정보가 올바르지 않습니다."));

        if(recommendListRequest.getRecommendListType().equals(RecommendListType.RECIEVED)){
            return MessageResponse.of(REQUEST_SUCCESS, recommendRepository.getRecommendsByToUser(user));
        } else {
            return MessageResponse.of(REQUEST_SUCCESS, recommendRepository.getRecommendsByFromUser(user));
        }

    }

    @Transactional
    public MessageResponse getRecommend(final long recommendId) {
        final Recommend recommend = recommendRepository.findById(recommendId)
                .orElseThrow(() -> new RecommendException("추천 정보를 찾을 수 없습니다."));

        recommendRepository.increasePlayCount(recommendId);

        return MessageResponse.of(REQUEST_SUCCESS, RecommendResponse.of(recommend, recommend.getToUser(), 0));
    }

}

package com.team.comma.spotify.recommend.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
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

    public MessageResponse addRecommend(final String accessToken, final RecommendRequest recommendRequest) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User fromUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("추천인 정보가 올바르지 않습니다."));

        Recommend buildEntity = createRecommend(fromUser, recommendRequest);
        recommendRepository.save(buildEntity);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public Recommend createRecommend(final User fromUser, final RecommendRequest recommendRequest) throws AccountException, PlaylistException{
        User toUser = userRepository.findByEmail(recommendRequest.getRecommendToEmail())
                .orElseThrow(() -> new AccountException("추천 대상 정보가 올바르지 않습니다."));

        Playlist playlist = playlistRepository.findById(recommendRequest.getRecommendPlaylistId())
                .orElseThrow(()-> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        return recommendRequest.toRecommendEntity(toUser, fromUser, playlist);
    }
}

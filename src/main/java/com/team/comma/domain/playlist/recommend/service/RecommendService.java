package com.team.comma.domain.playlist.recommend.service;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.recommend.constant.RecommendListType;
import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendRequest;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.playlist.recommend.exception.RecommendException;
import com.team.comma.domain.playlist.recommend.repository.RecommendRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;

import java.util.List;
import java.util.Optional;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlaylistService playlistService;

    private final RecommendRepository recommendRepository;
    private final PlaylistRepository playlistRepository;

    @Transactional
    public MessageResponse createPlaylistRecommend(final String accessToken, final RecommendRequest request)
            throws AccountException {
        final String userEmail = jwtTokenProvider.getUserPk(accessToken);
        final User fromUser = userService.findUserOrThrow(userEmail);

        final Playlist playlist = playlistService.findPlaylistOrThrow(request.getPlaylistId());
        Recommend recommend;
        if(request.getRecommendType().equals(RecommendType.FOLLOWING)){
            final User toUser = userService.findUserOrThrow(request.getToUserEmail());
            findRecommendThenThrow(playlist, toUser);

            recommend = Recommend.createRecommend(request.getComment(), playlist, toUser);
        } else {
            recommend = Recommend.createRecommend(request.getComment(), playlist);
        }
        recommendRepository.save(recommend);

        playlistRepository.updateRecommendCountByPlaylistId(playlist.getId());

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findAllPlaylistRecommends(final String accessToken, final RecommendListType recommendListType) throws AccountException {
        final String userEmail = jwtTokenProvider.getUserPk(accessToken);
        final User user = userService.findUserOrThrow(userEmail);

        List<RecommendResponse> result;
        if(recommendListType.equals(RecommendListType.RECIEVED)){
            result = findAllRecommendsByToUser(user);
        } else if(recommendListType.equals(RecommendListType.SENDED)){
            result = findAllRecommendsByFromUser(user);
        } else {
            result = findAllRecommendsByToAnonymous();
        }

        return MessageResponse.of(REQUEST_SUCCESS, result);
    }

    @Transactional
    public MessageResponse findRecommend(final String accessToken, final long recommendId) {
        final String userEmail = jwtTokenProvider.getUserPk(accessToken);
        final User user = userService.findUserOrThrow(userEmail);

        Recommend recommend = findRecommendOrThrow(recommendId);
        recommend.modifyPlayCount();

        RecommendResponse response;
        if(recommend.getToUser() == user){
            response = RecommendResponse.of(recommend, recommend.getPlaylist().getUser());
        } else {
            response = RecommendResponse.of(recommend, recommend.getToUser());
        }

        return MessageResponse.of(REQUEST_SUCCESS, response);
    }

    public List<RecommendResponse> findAllRecommendsByToUser(final User user){
        return recommendRepository.findAllRecommendsByToUser(user);
    }

    public List<RecommendResponse> findAllRecommendsByFromUser(final User user){
        return recommendRepository.findAllRecommendsByFromUser(user);
    }

    public List<RecommendResponse> findAllRecommendsByToAnonymous(){
        return recommendRepository.findAllRecommendsByToAnonymous();
    }

    public Recommend findRecommendOrThrow(final long recommendId) {
        return recommendRepository.findById(recommendId)
                .orElseThrow(() -> new RecommendException("추천 정보를 찾을 수 없습니다."));
    }

    public void findRecommendThenThrow(Playlist playlist, User user) {
        Optional<Recommend> recommend = recommendRepository.findByPlaylistAndToUser(playlist, user);
        if(recommend.isPresent()){
            throw new RecommendException("사용자에게 이미 추천한 플레이리스트 입니다.");
        }
    }

}

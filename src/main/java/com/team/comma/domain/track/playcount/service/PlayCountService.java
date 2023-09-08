package com.team.comma.domain.track.playcount.service;

import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.repository.TrackPlayCountRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class PlayCountService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TrackPlayCountRepository trackPlayCountRepository;
    private final TrackRepository trackRepository;
    private final TrackService trackService;

    public MessageResponse findMostListenedTrack(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByMostListenedTrack(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse findMostListenedTrackByFriend(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByFriend(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    @Transactional
    public MessageResponse modifyPlayCount(String accessToken , String trackId) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        TrackPlayCount trackPlayCount = findTrackPlayCountOrSave(userEmail , trackId);

        trackPlayCount.updatePlayCount();

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public TrackPlayCount findTrackPlayCountOrSave(String userEmail , String trackId) throws AccountException {
        Optional<TrackPlayCount> trackPlayCount = trackPlayCountRepository.findTrackPlayCountByUserEmail(userEmail , trackId);

        if(!trackPlayCount.isPresent()) {
            Track track = trackRepository.findBySpotifyTrackId(trackId)
                    .orElseGet(() -> trackService.findTrackOrSave(trackId));

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new AccountException("사용자 정보를 찾을 수 없습니다."));

            return trackPlayCountRepository.save(TrackPlayCount.createTrackPlayCount(track , user));
        }

        return trackPlayCount.get();
    }
}

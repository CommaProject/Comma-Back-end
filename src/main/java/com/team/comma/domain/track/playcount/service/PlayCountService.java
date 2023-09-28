package com.team.comma.domain.track.playcount.service;

import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.repository.TrackPlayCountRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class PlayCountService {

    private final TrackPlayCountRepository trackPlayCountRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final TrackService trackService;

    @Transactional
    public MessageResponse createTrackPlay(String accessToken , String spotifyTrackId) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userService.findUserOrThrow(userEmail);
        Track track = trackService.findTrackOrSave(spotifyTrackId);
        TrackPlayCount trackPlayCount = TrackPlayCount.createTrackPlayCount(track, user);

        trackPlayCountRepository.save(trackPlayCount);

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

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

}

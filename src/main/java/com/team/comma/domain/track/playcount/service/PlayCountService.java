package com.team.comma.domain.track.playcount.service;

import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountRequest;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.repository.TrackPlayCountRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.track.playcount.domain.TrackPlayCount.createTrackPlayCount;
import static com.team.comma.domain.track.playcount.dto.TrackPlayCountRequest.createTrackPlayCountRequest;
import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
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

    public TrackPlayCount findTrackPlayCountOrSave(String userEmail , String trackId) {
        Optional<TrackPlayCount> trackPlayCount = trackPlayCountRepository.findTrackPlayCountByUserEmail(userEmail , trackId);

        if(!trackPlayCount.isPresent()) {
            Track track = trackRepository.findBySpotifyTrackId(trackId)
                    .orElseGet(() -> trackService.findTrackOrSave(trackId));

            User user = userRepository.findUserByEmail(userEmail)
                    .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

            TrackPlayCountRequest request = createTrackPlayCountRequest(track);
            return trackPlayCountRepository.save(createTrackPlayCount(request , user));
        }

        return trackPlayCount.get();
    }
}

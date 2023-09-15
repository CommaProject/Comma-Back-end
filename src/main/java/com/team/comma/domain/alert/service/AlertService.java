package com.team.comma.domain.alert.service;

import com.team.comma.domain.alert.dto.AlertResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;

@Service
@RequiredArgsConstructor
public class AlertService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 60 second
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private Map<Long , SseEmitter> session = new HashMap<>();

    public SseEmitter subscribeAlert(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        if(session.containsKey(user.getId())) {
            session.remove(user.getId());
        }
        session.put(user.getId() , emitter);

        sendToClient(emitter , user.getId() , "EventStream Created. [userId = " + user.getId() + " ]");

        return emitter;
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void sendPlaylistAtSpecificTime() {
        List<AlertResponse> result = playlistRepository.findAllPlaylistsByAlertTime(LocalTime.now());

        for(AlertResponse playlist : result) {
            long id = playlist.getUserId();

            SseEmitter sseEmitter = session.get(id);
            sendToClient(sseEmitter , id , playlist);
        }
    }

    private void sendToClient(SseEmitter emitter, long id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(Long.toString(id))
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            throw new RuntimeException("SSE 연결을 실패했습니다.");
        }
    }
}

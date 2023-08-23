package com.team.comma.domain.playlist.service;

import com.team.comma.domain.playlist.dto.playlist.PlaylistUpdateRequest;
import com.team.comma.domain.playlist.repository.playlist.PlaylistRepository;
import com.team.comma.domain.playlist.domain.Playlist;
import com.team.comma.domain.playlist.exception.PlaylistException;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.user.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public MessageResponse getPlaylists(final String accessToken) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName)
            .orElseThrow(() -> new AccountException("정보가 올바르지 않습니다."));

        return MessageResponse.of(REQUEST_SUCCESS,
                playlistRepository.getPlaylistsByUser(user));
    }

    @Transactional
    public MessageResponse updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest) {
        Playlist playlist = playlistRepository.findById(playlistUpdateRequest.getId()).orElseThrow(
            () -> new EntityNotFoundException("플레이리스트를 찾을 수 없습니다."));
        playlist.updatePlaylist(playlistUpdateRequest);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse updatePlaylistAlarmFlag(long playlistId, boolean alarmFlag) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        playlistRepository.updateAlarmFlag(playlistId, alarmFlag);
        return MessageResponse.of(PLAYLIST_ALARM_UPDATED);
    }

    @Transactional
    public MessageResponse updatePlaylistsDelFlag(List<Long> playlistIdList) {
        for(Long playlistId : playlistIdList){
            Playlist playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));
        }
        for(Long playlistId : playlistIdList){
            playlistRepository.deletePlaylist(playlistId);
        }
        return MessageResponse.of(PLAYLIST_DELETED);
    }

    public MessageResponse<Integer> getTotalDurationTimeMsByPlaylist(Long playlistId) {
        return MessageResponse.of(
                REQUEST_SUCCESS,
                playlistRepository.getTotalDurationTimeMsWithPlaylistId(playlistId)
        );
    }

}

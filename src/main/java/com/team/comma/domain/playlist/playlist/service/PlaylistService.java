package com.team.comma.domain.playlist.playlist.service;

import com.team.comma.domain.alertday.service.AlertDayService;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.dto.PlaylistModifyRequest;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackService trackService;
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final AlertDayService alertDayService;

    public MessageResponse createPlaylist(final String accessToken, final String spotifyTrackId) {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        Track track = trackService.findTrackOrSave(spotifyTrackId);

        Playlist playlist = Playlist.buildPlaylist(user);
        playlist.addPlaylistTrack(track);
        playlistRepository.save(playlist);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findAllPlaylists(final String accessToken) {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName)
            .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        return MessageResponse.of(REQUEST_SUCCESS,
                playlistRepository.findAllPlaylistsByUser(user));
    }

    public MessageResponse findPlaylist(final long playlistId) {
        PlaylistResponse result = playlistRepository.findPlaylistByPlaylistId(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        return MessageResponse.of(REQUEST_SUCCESS, result);
    }

    public Playlist findPlaylistOrThrow(final long playlistId){
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));
    }

    public MessageResponse findTotalDurationTimeMsByPlaylist(final long playlistId) {
        return MessageResponse.of(REQUEST_SUCCESS,
                playlistRepository.findTotalDurationTimeMsByPlaylistId(playlistId));
    }

    @Transactional
    public MessageResponse modifyPlaylistAlarmDayAndTime(PlaylistModifyRequest request) {
        Playlist playlist = findPlaylistOrThrow(request.getPlaylistId());
        playlist.modifyAlarmStartTime(request);

        alertDayService.modifyAlertDays(playlist, request.getAlarmDays());

        playlist.modifyAlarmStartTime(request);
        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse modifyPlaylistTitle(PlaylistModifyRequest request) {
        Playlist playlist = findPlaylistOrThrow(request.getPlaylistId());
        playlist.modifyPlaylistTitle(request);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse modifyPlaylistAlarmFlag(PlaylistModifyRequest request) {
        Playlist playlist = findPlaylistOrThrow(request.getPlaylistId());
        playlist.modifyAlarmFlag();

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse modifyPlaylistsDelFlag(List<Long> playlistIdList) {
        for(Long playlistId : playlistIdList){
            findPlaylistOrThrow(playlistId);
        }
        playlistRepository.deletePlaylists(playlistIdList);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}

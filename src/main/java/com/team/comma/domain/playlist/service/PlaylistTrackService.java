package com.team.comma.domain.playlist.service;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.domain.playlist.repository.playlist.PlaylistRepository;
import com.team.comma.domain.playlist.repository.track.PlaylistTrackRepository;
import com.team.comma.domain.playlist.domain.PlaylistTrack;
import com.team.comma.domain.playlist.exception.PlaylistException;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.domain.Playlist;
import com.team.comma.domain.playlist.dto.track.PlaylistTrackSaveRequestDto;
import com.team.comma.domain.track.domain.Track;
import com.team.comma.domain.track.dto.TrackRequest;
import com.team.comma.domain.track.repository.TrackRepository;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.user.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;

import java.util.Optional;
import java.util.Set;
import javax.security.auth.login.AccountException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public MessageResponse removePlaylistAndTrack(Set<Long> trackIdList, Long playlistId) {
        int deleteCount = 0;

        for (Long trackId : trackIdList) {
            playlistTrackRepository.findByTrackIdAndPlaylistId(
                trackId, playlistId);

            deleteCount += playlistTrackRepository.
                deletePlaylistTrackByTrackIdAndPlaylistId(trackId, playlistId);
        }
        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage(),
            deleteCount
        );
    }

    public MessageResponse savePlaylistTrackList(PlaylistTrackSaveRequestDto dto, String accessToken)
        throws AccountException {

        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User findUser = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (dto.getPlaylistIdList().isEmpty()){
            Playlist playlist = dto.toPlaylistEntity(findUser);
            playlistRepository.save(playlist);

            for (TrackRequest trackRequest : dto.getTrackList()) {
                addTrackToPlaylist(playlist,trackRequest);
            }
        } else {
            for (Long playlistId : dto.getPlaylistIdList()){
                Playlist playlist = playlistRepository.findById(playlistId)
                        .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

                for (TrackRequest trackRequest : dto.getTrackList()) {
                    addTrackToPlaylist(playlist,trackRequest);
                }
            }
        }

        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage()
        );
    }

    public void addTrackToPlaylist(Playlist playlist, TrackRequest trackRequest){
        Optional<Track> optionalTrack = trackRepository.findBySpotifyTrackId(trackRequest.getSpotifyTrackId());
        Track track = (optionalTrack.isEmpty() ? trackRepository.save(trackRequest.toTrackEntity()) : optionalTrack.get());
        int maxPlaySequence = playlistTrackRepository.
                findMaxPlaySequenceByPlaylistId(playlist.getId())
                .orElse(0);

        PlaylistTrack eachPlaylistTrack = PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .trackAlarmFlag(true)
                .playSequence(maxPlaySequence + 1)
                .build();
        playlistTrackRepository.save(eachPlaylistTrack);
    }

    public MessageResponse getPlaylistTracks(long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        return MessageResponse.of(REQUEST_SUCCESS,
                playlistTrackRepository.getPlaylistTracksByPlaylist(playlist));
    }

}

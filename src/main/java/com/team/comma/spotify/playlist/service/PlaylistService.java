package com.team.comma.spotify.playlist.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.Exception.PlaylistException;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.track.service.TrackService;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCode.PLAYLIST_ALARM_UPDATED;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public List<PlaylistResponse> getPlaylist(final String accessToken) {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName);
        List<Playlist> playlists = playlistRepository.findAllByUser(user); // email로 playlist 조회
        return createPlaylistResponse(playlists);
    }

    public List<PlaylistResponse> createPlaylistResponse(List<Playlist> playlists){
        List<PlaylistResponse> result = new ArrayList<>();
        for(Playlist playlist : playlists){
            List<PlaylistTrackResponse> trackList = createTrackResponse(playlist.getPlaylistTrackList()); // playlist의 track list
            result.add(PlaylistResponse.of(playlist, trackList));
        }
        return result;
    }

    public List<PlaylistTrackResponse> createTrackResponse(List<PlaylistTrack> playlistTrackList){
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            TrackService trackService = new TrackService();
            List<PlaylistTrackArtistResponse> artistList = trackService.createArtistResponse(playlistTrack.getTrack().getTrackArtistList()); // track의 artist list
            result.add(PlaylistTrackResponse.of(playlistTrack.getTrack(), playlistTrack.getTrackAlarmFlag(), artistList));
        }
        return result;
    }

    @Transactional
    public MessageResponse updateAlarmFlag(long playlistId, boolean alarmFlag) throws PlaylistException{
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
        Playlist playlist = optionalPlaylist.orElseThrow(() -> new PlaylistException("알람 설정 변경에 실패했습니다. 플레이리스트를 찾을 수 없습니다."));

        playlistRepository.updateAlarmFlag(playlistId, alarmFlag);
        return MessageResponse.of(PLAYLIST_ALARM_UPDATED, "알람 설정이 변경되었습니다.");
    }

}

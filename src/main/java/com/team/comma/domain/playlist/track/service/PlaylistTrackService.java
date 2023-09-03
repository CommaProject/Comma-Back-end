package com.team.comma.domain.playlist.track.service;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.track.track.domain.Track;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistService playlistService;
    private final TrackService trackService;

    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistRepository playlistRepository;

    public MessageResponse createPlaylistTrack(PlaylistTrackRequest playlistTrackRequest) {
        Track track = trackService.findTrackOrSave(playlistTrackRequest.getSpotifyTrackId());
        for(long playlistId : playlistTrackRequest.getPlaylistIdList()){
            Playlist playlist = playlistService.findPlaylistOrThrow(playlistId);
            PlaylistTrack playlistTrack = PlaylistTrack.buildPlaylistTrack(playlist,track);
            playlistTrackRepository.save(playlistTrack);
        }

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findPlaylistTrack(long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        return MessageResponse.of(REQUEST_SUCCESS,
                playlistTrackRepository.getPlaylistTracksByPlaylist(playlist));
    }

    public MessageResponse removePlaylistAndTrack(Set<Long> trackIdList, Long playlistId) {
        int deleteCount = 0;

        for (Long trackId : trackIdList) {
            playlistTrackRepository.findByTrackIdAndPlaylistId(
                    trackId, playlistId);

            deleteCount += playlistTrackRepository.
                    deletePlaylistTrackByTrackIdAndPlaylistId(trackId, playlistId);
        }
        return MessageResponse.of(REQUEST_SUCCESS,deleteCount);
    }
}

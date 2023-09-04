package com.team.comma.domain.playlist.track.service;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackModifyRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.track.exception.PlaylistTrackException;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.track.track.domain.Track;

import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistService playlistService;
    private final TrackService trackService;

    private final PlaylistTrackRepository playlistTrackRepository;

    public MessageResponse createPlaylistTrack(PlaylistTrackRequest playlistTrackRequest) {
        Track track = trackService.findTrackOrSave(playlistTrackRequest.getSpotifyTrackId());
        for(long playlistId : playlistTrackRequest.getPlaylistIdList()){
            Playlist playlist = playlistService.findPlaylistOrThrow(playlistId);
            PlaylistTrack playlistTrack = PlaylistTrack.buildPlaylistTrack(playlist,track);
            playlistTrackRepository.save(playlistTrack);
        }

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findPlaylistTrack(final long playlistId) {
        Playlist playlist = playlistService.findPlaylistOrThrow(playlistId);
        List<PlaylistTrackResponse> playlistTrackResponses =
                playlistTrackRepository.getPlaylistTracksByPlaylist(playlist);

        return MessageResponse.of(REQUEST_SUCCESS, playlistTrackResponses);
    }

    public PlaylistTrack findPlaylistTrackOrThrow(final long playlistId, final long trackId){
        return playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId)
                .orElseThrow(() -> new PlaylistTrackException("플레이리스트의 트랙을 찾을 수 없습니다."));
    }

    @Transactional
    public MessageResponse modifyPlaylistTrackAlarmFlag(PlaylistTrackModifyRequest playlistTrackModifyRequest){
        PlaylistTrack playlistTrack = findPlaylistTrackOrThrow(
                playlistTrackModifyRequest.getPlaylistId(), playlistTrackModifyRequest.getTrackId());
        playlistTrack.modifyTrackAlarmFlag();

        return MessageResponse.of(REQUEST_SUCCESS, playlistTrack.getTrackAlarmFlag());
    }

    @Transactional
    public MessageResponse deletePlaylistTrack(final Set<Long> trackIdList, final Long playlistId) {
        int deleteCount = 0;

        for (Long trackId : trackIdList) {
            playlistTrackRepository.findByPlaylistIdAndTrackId(
                    playlistId, trackId);

            deleteCount += playlistTrackRepository.
                    deletePlaylistTrackByTrackIdAndPlaylistId(trackId, playlistId);
        }
        return MessageResponse.of(REQUEST_SUCCESS,deleteCount);
    }
}

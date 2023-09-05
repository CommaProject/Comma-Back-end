package com.team.comma.domain.playlist.track.service;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.domain.playlist.playlist.service.PlaylistService;
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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistService playlistService;
    private final TrackService trackService;

    private final PlaylistTrackRepository playlistTrackRepository;

    public MessageResponse createPlaylistTrack(final List<Long> playlistIdList, final String spotifyTrackId) {
        Track track = trackService.findTrackOrSave(spotifyTrackId);
        for(long playlistId : playlistIdList){
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

    public PlaylistTrack findPlaylistTrackOrThrow(final long playlistTrackId){
        return playlistTrackRepository.findById(playlistTrackId)
                .orElseThrow(() -> new PlaylistTrackException("플레이리스트의 트랙을 찾을 수 없습니다."));
    }

    @Transactional
    public MessageResponse modifyPlaylistTrackSequence(final List<Long> playlistTrackIdList){
        int count = 1;
        for(long playlistTrackId : playlistTrackIdList){
            PlaylistTrack playlistTrack = findPlaylistTrackOrThrow(playlistTrackId);
            count = playlistTrack.modifyPlaySequence(count);
        }
        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse modifyPlaylistTrackAlarmFlag(final long playlistTrackId){
        PlaylistTrack playlistTrack = findPlaylistTrackOrThrow(playlistTrackId);
        playlistTrack.modifyTrackAlarmFlag();

        return MessageResponse.of(REQUEST_SUCCESS, playlistTrack.getTrackAlarmFlag());
    }

    // 수정
    @Transactional
    public MessageResponse deletePlaylistTrack(final List<Long> playlistTrackIds) {
        for (long playlistTrackId : playlistTrackIds) {
            PlaylistTrack playlistTrack = findPlaylistTrackOrThrow(playlistTrackId);
            playlistTrackRepository.delete(playlistTrack);
        }
        return MessageResponse.of(REQUEST_SUCCESS);
    }
}

package com.team.comma.domain.playlist.track.service;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.track.exception.PlaylistTrackException;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.message.MessageResponse;
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
            for(PlaylistTrack playlistTrack : playlist.getPlaylistTrackList()){
                if(playlistTrack.getTrack().getSpotifyTrackId().equals(track.getSpotifyTrackId())){
                    throw new PlaylistException("이미 플레이리스트에 추가 된 트랙 입니다.");
                } else {
                    PlaylistTrack buildEntity = PlaylistTrack.buildPlaylistTrack(playlist,track);
                    playlistTrackRepository.save(buildEntity);
                }
            }
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
        int sequence = 1;
        for(long playlistTrackId : playlistTrackIdList){
            PlaylistTrack playlistTrack = findPlaylistTrackOrThrow(playlistTrackId);
            sequence = playlistTrack.modifyPlaySequence(sequence);
        }
        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse modifyPlaylistTrackAlarmFlag(final long playlistTrackId){
        PlaylistTrack playlistTrack = findPlaylistTrackOrThrow(playlistTrackId);
        playlistTrack.modifyTrackAlarmFlag();

        return MessageResponse.of(REQUEST_SUCCESS, playlistTrack.getTrackAlarmFlag());
    }

    @Transactional
    public MessageResponse deletePlaylistTracks(final List<Long> playlistTrackIds) {
        playlistTrackRepository.deletePlaylistTracksByIds(playlistTrackIds);
        return MessageResponse.of(REQUEST_SUCCESS);
    }
}

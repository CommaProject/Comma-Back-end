package com.team.comma.spotify.playlist.domain;

import com.team.comma.spotify.track.domain.Track;
import com.team.comma.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "playlist_tb")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String playlistTitle;

    private LocalTime alarmStartTime;

    private Boolean alarmFlag;

    private Integer listSequence;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistTrack> playlistTrackList;

    public void addPlaylistTrack(Track track) {
        if (playlistTrackList == null) {
            playlistTrackList = new ArrayList<>();
        }

        PlaylistTrack playlistTrack = PlaylistTrack.builder()
            .playlist(this)
            .track(track)
            .build();

        playlistTrackList.add(playlistTrack);
    }
}

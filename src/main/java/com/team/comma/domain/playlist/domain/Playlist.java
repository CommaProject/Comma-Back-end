package com.team.comma.domain.playlist.domain;

import com.team.comma.domain.playlist.dto.playlist.PlaylistUpdateRequest;
import com.team.comma.domain.track.domain.Track;
import com.team.comma.domain.user.domain.User;
import com.team.comma.global.converter.BooleanConverter;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "playlist_tb")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String playlistTitle;

    private LocalTime alarmStartTime;

    @ColumnDefault("false")
    private Boolean alarmFlag;

    private Integer listSequence;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private boolean delFlag = false;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "playlist")
    @Builder.Default
    private List<PlaylistTrack> playlistTrackList = new ArrayList<>();

    public void addPlaylistTrack(Track track) {
        PlaylistTrack playlistTrack = PlaylistTrack.builder()
            .playlist(this)
            .track(track)
            .build();

        playlistTrackList.add(playlistTrack);
    }

    public void updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest) {
        this.playlistTitle = playlistUpdateRequest.getPlaylistTitle();
        this.alarmStartTime = playlistUpdateRequest.getAlarmStartTime();
        this.listSequence = playlistUpdateRequest.getListSequence();
    }
}

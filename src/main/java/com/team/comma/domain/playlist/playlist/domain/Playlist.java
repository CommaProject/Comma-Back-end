package com.team.comma.domain.playlist.playlist.domain;

import com.team.comma.domain.playlist.playlist.dto.PlaylistModifyRequest;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
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

    public void modifyPlaylistTitle(PlaylistModifyRequest playlistModifyRequest) {
        this.playlistTitle = playlistModifyRequest.getPlaylistTitle();
    }

    public void modifyAlarmStartTime(PlaylistModifyRequest playlistModifyRequest) {
        this.alarmStartTime = playlistModifyRequest.getAlarmStartTime();
    }

    public void modifyAlarmFlag() {
        this.alarmFlag = !this.alarmFlag;
    }

    public static Playlist buildPlaylist(Long id, User user, String playlistTitle) {
        return Playlist.builder()
                .id(id)
                .playlistTitle(playlistTitle)
                .alarmStartTime(LocalTime.now())
                .alarmFlag(true)
                .delFlag(false)
                .user(user)
                .build();
    }

    public static Playlist createPlaylist(User user) {
        return buildPlaylist(null, user, "새로운 플레이리스트");
    }

    public static Playlist createPlaylist(Long id, User user) {
        return buildPlaylist(id, user, "새로운 플레이리스트");
    }
}

package com.team.comma.domain.playlist.track.domain;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.track.track.domain.Track;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "playlist_track_tb")
public class PlaylistTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

    @JoinColumn(name = "track_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Track track;

    @ColumnDefault("false")
    private Boolean trackAlarmFlag;

    private Integer playSequence;

    public void modifyTrackAlarmFlag() {
        this.trackAlarmFlag = !this.trackAlarmFlag;
    }

    public int modifyPlaySequence(int playSequence) {
        this.playSequence = playSequence;
        return playSequence + 1;
    }

    public static PlaylistTrack buildPlaylistTrack(Playlist playlist, Track track){
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .trackAlarmFlag(false)
                .build();
    }

}

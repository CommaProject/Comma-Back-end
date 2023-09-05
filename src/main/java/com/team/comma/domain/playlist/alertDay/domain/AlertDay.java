package com.team.comma.domain.playlist.alertDay.domain;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import jakarta.persistence.*;

import java.time.DayOfWeek;
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
@Table(name = "alert_day_tb")
public class AlertDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 1 ~ 7 : 월 ~ 토
     */
    private DayOfWeek alarmDay;

    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

    public static AlertDay buildAlertDay(Playlist playlist, DayOfWeek alarmDay){
        return AlertDay.builder()
                .playlist(playlist)
                .alarmDay(alarmDay)
                .build();
    }
}

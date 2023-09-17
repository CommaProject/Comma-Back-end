package com.team.comma.domain.playlist.alertday.service;

import com.team.comma.domain.playlist.alertDay.service.AlertDayService;
import com.team.comma.domain.playlist.alertDay.domain.AlertDay;
import com.team.comma.domain.playlist.alertDay.repository.AlertDayRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.user.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AlertDayServiceTest {

    @InjectMocks
    AlertDayService alertDayService;
    @Mock
    AlertDayRepository alertDayRepository;
    @Test
    void createAlertDay_Success() {
        // given
        User user = User.buildUser("userEmail");
        Playlist playlist = Playlist.buildPlaylist(user);
        List<AlertDay> alertDays = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(1)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(2)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(3)));
        doReturn(alertDays).when(alertDayRepository).findAllByPlaylist(playlist);

        // when
        alertDayService.createAlertDays(playlist, List.of(1,2,3));

        // then
        List<AlertDay> result = alertDayRepository.findAllByPlaylist(playlist);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getAlarmDay()).isEqualTo(DayOfWeek.of(1));
        assertThat(result.get(1).getAlarmDay()).isEqualTo(DayOfWeek.of(2));
        assertThat(result.get(2).getAlarmDay()).isEqualTo(DayOfWeek.of(3));

    }

    @Test
    void modifyAlertDays_Success() {
        // given
        User user = User.buildUser("userEmail");
        Playlist playlist = Playlist.buildPlaylist(user);
        List<AlertDay> alertDays = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(4)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(5)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(6)));
        doReturn(alertDays).when(alertDayRepository).findAllByPlaylist(playlist);

        // when
        alertDayService.modifyAlertDays(playlist, List.of(4, 5, 6));

        // then
        List<AlertDay> result = alertDayRepository.findAllByPlaylist(playlist);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getAlarmDay()).isEqualTo(DayOfWeek.of(4));
        assertThat(result.get(1).getAlarmDay()).isEqualTo(DayOfWeek.of(5));
        assertThat(result.get(2).getAlarmDay()).isEqualTo(DayOfWeek.of(6));

    }

    @Test
    void deleteAllAlertDays_Success() {
        // given
        User user = User.buildUser("userEmail");
        Playlist playlist = Playlist.buildPlaylist(user);
        List<AlertDay> alertDays = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(1)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(2)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(3)));
        alertDayRepository.saveAll(alertDays);

        // when
        alertDayService.deleteAlertDays(playlist);

        // then
        List<AlertDay> result = alertDayRepository.findAllByPlaylist(playlist);
        assertThat(result.size()).isEqualTo(0);

    }
}

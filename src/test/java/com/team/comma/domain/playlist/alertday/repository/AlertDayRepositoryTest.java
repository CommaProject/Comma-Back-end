package com.team.comma.domain.playlist.alertday.repository;

import com.team.comma.domain.playlist.alertday.domain.AlertDay;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlertDayRepositoryTest {

    @Autowired
    private AlertDayRepository alertDayRepository;
    @Test
    void saveAll_Success() {
        // given
        User user = User.buildUser();
        Playlist playlist = Playlist.buildPlaylist(user);
        List<AlertDay> alertDayList = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(1)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(2)));

        // when
        List<AlertDay> result = alertDayRepository.saveAll(alertDayList);

        // then
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void findAllByPlaylist() {
        // given
        User user = User.buildUser();
        Playlist playlist = Playlist.buildPlaylist(user);
        List<AlertDay> alertDayList = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(1)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(2)));
        alertDayRepository.saveAll(alertDayList);

        // when
        List<AlertDay> result = alertDayRepository.findAllByPlaylist(playlist);

        // then
        assertThat(result.size()).isEqualTo(2);

    }

}

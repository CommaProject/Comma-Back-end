package com.team.comma.domain.playlist.alertday.repository;

import com.team.comma.domain.alertday.repository.AlertDayRepository;
import com.team.comma.domain.playlist.alertDay.domain.AlertDay;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlertDayRepositoryTest {

    @Autowired
    private AlertDayRepository alertDayRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;

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
        userRepository.save(user);

        Playlist playlist = Playlist.buildPlaylist(user);
        playlistRepository.save(playlist);

        List<AlertDay> alertDayList = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(1)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(2)));
        alertDayRepository.saveAll(alertDayList);

        // when
        List<AlertDay> result = alertDayRepository.findAllByPlaylist(playlist);

        // then
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void deleteAllAlertDaysByPlaylist_Success() {
        // given
        User user = User.buildUser();
        userRepository.save(user);

        Playlist playlist = Playlist.buildPlaylist(user);
        playlistRepository.save(playlist);

        List<AlertDay> alertDayList = List.of(
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(1)),
                AlertDay.buildAlertDay(playlist, DayOfWeek.of(2)));
        alertDayRepository.saveAll(alertDayList);

        // when
        long result = alertDayRepository.deleteAlertDaysByPlaylist(playlist);

        // then
        assertThat(result).isEqualTo(2);

    }

}

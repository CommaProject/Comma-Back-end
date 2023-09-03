package com.team.comma.domain.playlist.alertday.service;

import com.team.comma.domain.playlist.alertday.domain.AlertDay;
import com.team.comma.domain.playlist.alertday.repository.AlertDayRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.user.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AlertDayServiceTest {

    @InjectMocks
    AlertDayService alertDayService;
    @Mock
    PlaylistService playlistService;
    @Mock
    AlertDayRepository alertDayRepository;

    @Test
    void createAlertDay_Success() {
        // given
        User user = User.buildUser();
        Playlist playlist = Playlist.buildPlaylist(user);
        doReturn(playlist).when(playlistService).findPlaylistOrThrow(anyLong());

        // when
        alertDayService.createAlertDay(playlist, List.of(1,2,3));

        // then
        List<AlertDay> alertDays = alertDayRepository.findAllByPlaylist(playlist);
        assertThat(alertDays.size()).isEqualTo(3);

    }
}

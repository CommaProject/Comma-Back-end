package com.team.comma.domain.playlist.alertDay.repository;

import com.team.comma.domain.playlist.playlist.domain.Playlist;

public interface AlertDayRepositoryCustom {

    long deleteAlertDaysByPlaylist(Playlist playlist);
}

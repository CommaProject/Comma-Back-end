package com.team.comma.domain.alertday.repository;

import com.team.comma.domain.playlist.playlist.domain.Playlist;

public interface AlertDayRepositoryCustom {

    long deleteAllAlertDaysByPlaylist(Playlist playlist);
}

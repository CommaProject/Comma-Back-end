package com.team.comma.domain.alertday.service;

import com.team.comma.domain.playlist.alertDay.domain.AlertDay;
import com.team.comma.domain.alertday.repository.AlertDayRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertDayService {

    private final AlertDayRepository alertDayRepository;

    @Transactional
    public void createAlertDays(Playlist playlist, List<Integer> alarmDays) {
        List<AlertDay> alertDays = new ArrayList<>();
        for(int alarmDay : alarmDays){
            alertDays.add(AlertDay.buildAlertDay(playlist, DayOfWeek.of(alarmDay)));
        }
        alertDayRepository.saveAll(alertDays);
    }

    @Transactional
    public void modifyAlertDays(Playlist playlist, List<Integer> alarmDays) {
        deleteAllAlertDaysByPlaylist(playlist);
        createAlertDays(playlist, alarmDays);
    }

    @Transactional
    public void deleteAllAlertDaysByPlaylist(Playlist playlist) {
        alertDayRepository.deleteAllAlertDaysByPlaylist(playlist);
    }

}

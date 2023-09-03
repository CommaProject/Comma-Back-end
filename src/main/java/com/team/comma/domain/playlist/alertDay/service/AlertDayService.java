package com.team.comma.domain.playlist.alertDay.service;

import com.team.comma.domain.playlist.alertDay.domain.AlertDay;
import com.team.comma.domain.playlist.alertDay.repository.AlertDayRepository;
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
    public void createAlertDay(Playlist playlist, List<Integer> alarmDays) {
        List<AlertDay> alertDays = new ArrayList<>();
        for(int alarmDay : alarmDays){
            alertDays.add(AlertDay.buildAlertDay(playlist, DayOfWeek.of(alarmDay)));
        }
        alertDayRepository.saveAll(alertDays);
    }

}

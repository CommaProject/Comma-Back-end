package com.team.comma.spotify.history.repository;

import com.team.comma.spotify.history.dto.HistoryResponse;

import java.util.List;

public interface SpotifyHistoryRepositoryCustom {
    List<HistoryResponse> getHistoryListByUserEmail(String userEmail);
    void deleteHistoryById(long id);
}

package com.team.comma.domain.spotify.history.repository;

import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.spotify.history.dto.HistoryResponse;

import java.util.List;

public interface HistoryRepositoryCustom {

    List<HistoryResponse> getHistoryListByUserEmail(String userEmail);

    void deleteHistoryById(long id);

    void deleteAllHistoryByUser(User user);
}

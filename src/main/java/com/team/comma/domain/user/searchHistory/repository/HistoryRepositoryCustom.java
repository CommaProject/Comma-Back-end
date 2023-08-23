package com.team.comma.domain.user.searchHistory.repository;

import com.team.comma.domain.user.searchHistory.dto.HistoryResponse;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;

public interface HistoryRepositoryCustom {

    List<HistoryResponse> getHistoryListByUserEmail(String userEmail);

    void deleteHistoryById(long id);

    void deleteAllHistoryByUser(User user);
}

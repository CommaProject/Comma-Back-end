package com.team.comma.domain.user.history.repository;

import com.team.comma.domain.user.history.dto.HistoryResponse;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;

public interface HistoryRepositoryCustom {

    List<HistoryResponse> getHistoryListByUserEmail(String userEmail);

    void deleteHistoryById(long id);

    void deleteAllHistoryByUser(User user);
}

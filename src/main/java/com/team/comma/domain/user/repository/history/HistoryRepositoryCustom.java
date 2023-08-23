package com.team.comma.domain.user.repository.history;

import com.team.comma.domain.user.dto.history.HistoryResponse;
import com.team.comma.domain.user.domain.User;

import java.util.List;

public interface HistoryRepositoryCustom {

    List<HistoryResponse> getHistoryListByUserEmail(String userEmail);

    void deleteHistoryById(long id);

    void deleteAllHistoryByUser(User user);
}

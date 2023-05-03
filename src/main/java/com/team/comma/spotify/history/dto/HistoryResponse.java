package com.team.comma.spotify.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryResponse {
    final private long id;
    final private String searchHistory;

}

package com.team.comma.domain.spotify.history.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HistoryResponse {
    private long id;
    private String searchHistory;

}

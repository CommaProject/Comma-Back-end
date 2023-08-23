package com.team.comma.domain.user.searchHistory.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HistoryRequest {
    private String searchHistory;
}

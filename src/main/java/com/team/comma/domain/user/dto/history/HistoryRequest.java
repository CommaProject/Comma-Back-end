package com.team.comma.domain.user.dto.history;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HistoryRequest {
    private String searchHistory;
}

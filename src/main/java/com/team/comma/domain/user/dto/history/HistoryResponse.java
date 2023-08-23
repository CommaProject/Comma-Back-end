package com.team.comma.domain.user.dto.history;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HistoryResponse {
    private long id;
    private String searchHistory;

}

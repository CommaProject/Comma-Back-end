package com.team.comma.domain.playlist.dto.recommend;

import com.team.comma.domain.playlist.constant.RecommendListType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendListRequest {
    private RecommendListType recommendListType;
}

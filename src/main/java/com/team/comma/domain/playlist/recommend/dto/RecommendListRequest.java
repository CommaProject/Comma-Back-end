package com.team.comma.domain.playlist.recommend.dto;

import com.team.comma.domain.playlist.recommend.constant.RecommendListType;
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

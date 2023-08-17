package com.team.comma.domain.spotify.archive.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArchiveRequest {
    private String content;
    private long playlistId;

}

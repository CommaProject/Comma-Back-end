package com.team.comma.domain.playlist.archive.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArchiveRequest {
    private long playlistId;
    private String comment;
}

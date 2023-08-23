package com.team.comma.domain.playlist.dto.archive;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArchiveRequest {
    private String content;
    private long playlistId;

}

package com.team.comma.domain.playlist.archive.repository;

import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.user.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ArchiveRepositoryCustom {

    List<ArchiveResponse> findArchiveByDateTime(User user, LocalDateTime startDate, LocalDateTime endDate);

}

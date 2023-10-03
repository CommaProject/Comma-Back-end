package com.team.comma.domain.playlist.archive.repository;

import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.user.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ArchiveRepository extends JpaRepository<Archive, Long>, ArchiveRepositoryCustom{

}

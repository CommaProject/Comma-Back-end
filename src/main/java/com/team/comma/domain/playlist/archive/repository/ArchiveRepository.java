package com.team.comma.domain.playlist.archive.repository;

import com.team.comma.domain.playlist.archive.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

}

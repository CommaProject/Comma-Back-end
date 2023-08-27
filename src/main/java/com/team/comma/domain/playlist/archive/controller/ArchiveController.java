package com.team.comma.domain.playlist.archive.controller;

import com.team.comma.domain.playlist.archive.dto.ArchiveRequest;
import com.team.comma.domain.playlist.archive.service.ArchiveService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archives")
public class ArchiveController {

    private final ArchiveService archiveService;

    @PostMapping
    public ResponseEntity<MessageResponse> createArchive(
            @CookieValue String accessToken,
            @RequestBody ArchiveRequest archiveRequest) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(archiveService.createArchive(accessToken, archiveRequest));
    }

    @GetMapping("{startDate}")
    public ResponseEntity<MessageResponse> findAllArchiveByDate(
            @CookieValue String accessToken,
            @PathVariable LocalDateTime startDate) throws AccountException {
        return ResponseEntity.ok()
                .body(archiveService.findAllArchiveByDate(accessToken, startDate));
    }

}

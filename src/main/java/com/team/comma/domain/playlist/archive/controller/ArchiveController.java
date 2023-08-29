package com.team.comma.domain.playlist.archive.controller;

import com.team.comma.domain.playlist.archive.dto.ArchiveRequest;
import com.team.comma.domain.playlist.archive.service.ArchiveService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import java.time.LocalDate;

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

    @GetMapping("/{startDate}/{endDate}")
    public ResponseEntity<MessageResponse> findArchiveByDate(
            @CookieValue String accessToken,
            @PathVariable LocalDate startDate,
            @PathVariable LocalDate endDate) throws AccountException {
        return ResponseEntity.ok()
                .body(archiveService.findArchiveByDate(accessToken, startDate, endDate));
    }

}

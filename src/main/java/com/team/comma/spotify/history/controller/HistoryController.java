package com.team.comma.spotify.history.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spotify")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/history")
    public ResponseEntity<MessageResponse> getHistoryListByToken(@CookieValue("accessToken") String token) throws AccountException {
        return ResponseEntity.ok().body(historyService.getHistoryList(token));
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<MessageResponse> deleteUserHistory(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(historyService.deleteHistory(id));
    }

    @DeleteMapping("/all-history")
    public ResponseEntity<MessageResponse> deleteUserAllHistory(@CookieValue("accessToken") String token) throws AccountException {
        return ResponseEntity.status(HttpStatus.OK).body(historyService.deleteAllHistory(token));
    }

    @PostMapping("/history")
    public ResponseEntity<MessageResponse> addHistory(@RequestBody HistoryRequest historyRequest
            , @CookieValue("accessToken") String token) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.addHistory(historyRequest , token));
    }

}

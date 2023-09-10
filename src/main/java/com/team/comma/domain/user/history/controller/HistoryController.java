package com.team.comma.domain.user.history.controller;

import com.team.comma.domain.user.history.dto.HistoryRequest;
import com.team.comma.domain.user.history.service.HistoryService;
import com.team.comma.global.common.dto.MessageResponse;
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

    @GetMapping("/histories")
    public ResponseEntity<MessageResponse> getHistoryListByToken(@CookieValue("accessToken") String token) throws AccountException {
        return ResponseEntity.ok().body(historyService.getHistoryList(token));
    }

    @DeleteMapping("/histories/{id}")
    public ResponseEntity<MessageResponse> deleteUserHistory(@PathVariable long id) {
        return ResponseEntity.ok().body(historyService.deleteHistory(id));
    }

    @DeleteMapping("/all-histories")
    public ResponseEntity<MessageResponse> deleteUserAllHistory(@CookieValue("accessToken") String token) throws AccountException {
        return ResponseEntity.ok().body(historyService.deleteAllHistory(token));
    }

    @PostMapping("/histories")
    public ResponseEntity<MessageResponse> addHistory(@RequestBody HistoryRequest historyRequest
            , @CookieValue("accessToken") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.addHistory(historyRequest , token));
    }

}

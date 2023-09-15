package com.team.comma.domain.alert.controller;

import com.team.comma.domain.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping(value = "/alert/subscribe" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity subscribeAlert(@CookieValue String accessToken) {
        SseEmitter sseEmitter = alertService.subscribeAlert(accessToken);

        return ResponseEntity.ok().body(sseEmitter);
    }

}

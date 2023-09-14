package com.team.comma.domain.alert.controller;

import com.team.comma.domain.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping(value = "/alert/subscribe" , produces = "text/event-stream")
    public ResponseEntity subscribeAlert(@CookieValue String accessToken) {
        alertService.subscribeAlert(accessToken);

        return ResponseEntity.ok().build();
    }

}

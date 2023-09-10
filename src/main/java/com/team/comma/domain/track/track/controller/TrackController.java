package com.team.comma.domain.track.track.controller;

import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/tracks")
@RestController
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/favorites")
    public MessageResponse findTrackByMostFavorite() {
        return trackService.findTrackByMostFavorite();
    }

}

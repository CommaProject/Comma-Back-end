package com.team.comma.domain.track.playcount.controller;

import com.team.comma.domain.track.playcount.service.PlayCountService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/tracks")
@RestController
@RequiredArgsConstructor
public class PlayCountController {

    private final PlayCountService playCountService;

    @PatchMapping("/play-count/{trackId}")
    public MessageResponse modifyPlayCount(@CookieValue String accessToken , @PathVariable String trackId) throws AccountException {
        return playCountService.modifyPlayCount(accessToken , trackId);
    }

    @GetMapping
    public MessageResponse findMostListenedTrack(@CookieValue String accessToken) {
        return playCountService.findMostListenedTrack(accessToken);
    }

    @GetMapping("/friends")
    public MessageResponse findMostListenedTrackByFriend(@CookieValue String accessToken) {
        return playCountService.findMostListenedTrackByFriend(accessToken);
    }

}

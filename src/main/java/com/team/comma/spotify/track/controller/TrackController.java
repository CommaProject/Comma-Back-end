package com.team.comma.spotify.track.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/tracks")
@RestController
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping
    public MessageResponse countPlayCount() {
        return null;
    }

}


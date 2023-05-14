package com.team.comma.follow.service;

import com.team.comma.follow.repository.FollowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowingService {

    private final FollowingRepository followingRepository;

    public boolean isFollowed(String userEmail) {


        return false;
    }

}

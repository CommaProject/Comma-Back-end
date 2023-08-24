package com.team.comma.domain.user.following.repository;

import com.team.comma.domain.user.following.domain.Following;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> , FollowingRepositoryCustom {

}

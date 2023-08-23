package com.team.comma.domain.user.followship.repository;

import com.team.comma.domain.user.followship.domain.Following;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> , FollowingRepositoryCustom {

}

package com.team.comma.domain.user.repository.following;

import com.team.comma.domain.user.domain.Following;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> , FollowingRepositoryCustom {

}

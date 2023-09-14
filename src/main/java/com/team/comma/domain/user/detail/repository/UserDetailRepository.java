package com.team.comma.domain.user.detail.repository;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {

    Optional<UserDetail> findUserDetailByUser(User user);
}

package com.team.comma.domain.user.detail.repository;

import com.team.comma.domain.user.detail.domain.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
}

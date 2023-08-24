package com.team.comma.domain.user.user.repository;

import com.team.comma.domain.user.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    List<User> searchUserByUserNameAndNickName(String name);
    Optional<User> findByEmail(String email);

}

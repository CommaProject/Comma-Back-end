package com.team.comma.domain.user.repository.user;

import com.team.comma.domain.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    List<User> searchUserByUserNameAndNickName(String name);
    Optional<User> findByEmail(String email);

}

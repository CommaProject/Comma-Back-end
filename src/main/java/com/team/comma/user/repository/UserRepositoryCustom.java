package com.team.comma.user.repository;

import com.team.comma.user.domain.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> searchUserByUserNameAndNickName(String name);
    User findByEmail(String email);

}

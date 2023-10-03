package com.team.comma.domain.user.user.repository;

import com.team.comma.domain.user.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findUserByEmail(String email);

}
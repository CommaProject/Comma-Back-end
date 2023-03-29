package com.team.comma.repository;

import com.team.comma.entity.oauth.User;
import com.team.comma.enumType.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByIdAndAuthProvider(String id, AuthProvider authProvider);
}

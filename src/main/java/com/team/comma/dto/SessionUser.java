package com.team.comma.dto;

import com.team.comma.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public final class SessionUser implements Serializable {
    final private String email;
    final private String name;

    private SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public static SessionUser of(User user) {
        return new SessionUser(user);
    }
}

package com.team.comma.domain.user.repository;

import com.team.comma.domain.user.domain.User;

import java.util.List;

public interface FavoriteGenreRepositoryCustom {
    List<String> findByGenreNameList(User user);
}

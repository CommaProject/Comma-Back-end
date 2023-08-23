package com.team.comma.domain.user.searchHistory.repository;


import com.team.comma.domain.user.searchHistory.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

}

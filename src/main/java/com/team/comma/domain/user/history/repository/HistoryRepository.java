package com.team.comma.domain.user.history.repository;


import com.team.comma.domain.user.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

}

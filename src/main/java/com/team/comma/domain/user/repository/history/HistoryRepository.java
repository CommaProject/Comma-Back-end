package com.team.comma.domain.user.repository.history;


import com.team.comma.domain.user.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

}

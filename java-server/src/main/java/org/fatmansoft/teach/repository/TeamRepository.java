package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Integer> {
    Optional<Team> findByNum(String num);
    @Query(value =" select max(num) from Team")
    String getMaxNum();
    @Query(value="from Team where taskId =?1 order by num")
    List<Team> getSortedTeamList(Integer taskId );
    List<Team> findByTaskId(Integer taskId);
}

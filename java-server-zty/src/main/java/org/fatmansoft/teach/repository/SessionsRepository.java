package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionsRepository extends JpaRepository<Sessions,Integer> {
    @Query(value = "select max(id) from Sessions  ")
    Integer getMaxId();

    @Query(value="from Sessions  where week=?1 and day=?2")
    Optional<Sessions> findBySessions(Integer week, Integer day);


}

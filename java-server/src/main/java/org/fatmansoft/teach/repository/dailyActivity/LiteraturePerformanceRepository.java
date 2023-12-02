package org.fatmansoft.teach.repository.dailyActivity;

import org.fatmansoft.teach.models.dailyActivity.LiteraturePerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiteraturePerformanceRepository extends JpaRepository<LiteraturePerformance,Integer> {
    @Query(value = "select max(performanceId) from LiteraturePerformance")
    Integer getMaxId();

    @Query(value= "from LiteraturePerformance where student.studentId= ?1")
    List<LiteraturePerformance> findLiteraturePerformanceByStudentId(Integer studentId);
    Optional<LiteraturePerformance> findByPerformanceId(Integer performanceId);

    @Query(value = "from LiteraturePerformance where ?1='' or day like %?1% ")
    List<LiteraturePerformance> findLiteraturePerformanceListByNumName(String day);
}

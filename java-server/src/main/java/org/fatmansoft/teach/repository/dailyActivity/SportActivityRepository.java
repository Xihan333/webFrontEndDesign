package org.fatmansoft.teach.repository.dailyActivity;

import org.fatmansoft.teach.models.dailyActivity.SportActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SportActivityRepository extends JpaRepository<SportActivity,Integer> {
    @Query(value = "select max(activityId) from SportActivity")
    Integer getMaxId();

    @Query(value= "from SportActivity where student.studentId= ?1")
    List<SportActivity> findSportActivityByStudentId(Integer studentId);

    Optional<SportActivity> findByActivityId(Integer activityId);

    @Query(value = "from SportActivity where ?1='' or day like %?1% or Title like %?1% ")
    List<SportActivity> findSportActivityListByNumName(String dayTitle);
}

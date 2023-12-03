package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "select max(activityId) from Activity")
    Integer getMaxId();

    @Query(value= "from Activity where student.studentId= ?1")
    List<Activity> findSportActivityByStudentId(Integer studentId);

    Optional<Activity> findByActivityId(Integer activityId);

    @Query(value = "from Activity where ?1='' or day like %?1% or Title like %?1% ")
    List<Activity> findSportActivityListByNumName(String dayTitle);
}

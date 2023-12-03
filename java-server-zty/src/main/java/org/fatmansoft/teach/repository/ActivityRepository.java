package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "select max(id) from Activity  ")
    Integer getMaxId();

//    Optional<Award> findByAwardName(String AwardName);

    @Query(value = "from Activity where ?1='' or activityName like %?1% ")
    List<Activity> findActivityListByName(String name);

    @Query(value = "from Activity where student.id=?1")
    List<Activity> findActivityListByStudentId(Integer studentId);

    @Query(value = "from Activity where ?1='' or activityName like %?1% or student.studentName like %?1% ")
    List<Activity> findActivityListByInput(String input);

    @Query(value = "from Activity where student.studentNum=?2 and ( ?1='' or activityName like %?1% or student.studentName like %?1% )")
    List<Activity> findActivityListByNameForStudent(String name, String studentNum);
}

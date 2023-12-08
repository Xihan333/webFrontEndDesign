package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,Integer> {
    @Query(value = "select max(achievementId) from Achievement")
    Integer getMaxId();

    @Query(value= "from Achievement where student.studentId= ?1")
    List<Achievement> findAchievementByStudentId(Integer studentId);

    Optional<Achievement> findByAchievementId(Integer achievementId);

    @Query(value = "from Achievement where ?1='' or  student.person.num like %?1% or student.person.name like %?1% ")
    List<Achievement> findAchievementListByStudentNumName(String numName);

    @Query(value= "from Achievement where status=0")
    List<Achievement> findWaitingAchievementList();

    @Query(value= "from Achievement where status=1")
    List<Achievement> findPassedAchievementList();

    @Query(value= "from Achievement where status=2")
    List<Achievement> findFailedAchievementList();

    @Query(value = "from Achievement where status=1 and ?1='' or  student.person.num like %?1% or student.person.name like %?1% ")
    List<Achievement> findPassedAchievementListByStudentNumName(String numName);

    @Query(value= "from Achievement where student.studentId= ?1 and status=1")
    List<Achievement> findPassedAchievementByStudentId(Integer studentId);

}

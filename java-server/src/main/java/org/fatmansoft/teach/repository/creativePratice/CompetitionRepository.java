package org.fatmansoft.teach.repository.creativePratice;

import org.fatmansoft.teach.models.creativePractice.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition,Integer> {
    @Query(value = "select max(competitionId) from Competition ")
    Integer getMaxId();

    @Query(value= "from Competition where student.studentId= ?1")
    List<Competition> findCompetitionByStudentId(Integer studentId);

    Optional<Competition> findByCompetitionId(Integer competitionId);

    @Query(value = "from Competition where ?1='' or  teacher.person.num like %?1% or teacher.person.name like %?1% ")
    List<Competition> findCompetitionListByTeacherNumName(String numName);
    @Query(value = "from Competition where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<Competition> findCompetitionListByStudentNumName(String numName);

    @Query(value= "from Competition where auditStatus=0")
    List<Competition> findWaitingCompetitionList();

    @Query(value= "from Competition where auditStatus=1")
    List<Competition> findPassedCompetitionList();

    @Query(value= "from Competition where auditStatus=2")
    List<Competition> findFailedCompetitionList();

    @Query(value= "from Competition where auditStatus=0")
    List<Competition> findAllCompetitionList();

    @Query(value= "from Competition where teacher.teacherId= ?1")
    List<Competition> findCompetitionByTeacherId(Integer teacherId);

}

package org.fatmansoft.teach.repository.creativePratice;

import org.fatmansoft.teach.models.creativePractice.ScientificPayoffs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScientificPayoffsRepository extends JpaRepository<ScientificPayoffs,Integer> {
    @Query(value = "select max(scientificPayoffsId) from ScientificPayoffs ")
    Integer getMaxId();

    @Query(value= "from ScientificPayoffs where student.studentId= ?1")
    List<ScientificPayoffs> findScientificPayoffsByStudentId(Integer studentId);

    @Query(value= "from ScientificPayoffs where teacher.teacherId= ?1")
    List<ScientificPayoffs> findScientificPayoffsByTeacherId(Integer teacherId);

    Optional<ScientificPayoffs> findByScientificPayoffsId(Integer scientificPayoffsId);

    @Query(value = "from ScientificPayoffs where ?1='' or  teacher.person.num like %?1% or teacher.person.name like %?1% ")
    List<ScientificPayoffs> findScientificPayoffsListByTeacherNumName(String numName);
    @Query(value = "from ScientificPayoffs where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<ScientificPayoffs> findScientificPayoffsListByStudentNumName(String numName);

    @Query(value= "from ScientificPayoffs where auditStatus=0")
    List<ScientificPayoffs> findWaitingScientificPayoffsList();

    @Query(value= "from ScientificPayoffs where auditStatus=1")
    List<ScientificPayoffs> findPassedScientificPayoffsList();

    @Query(value= "from ScientificPayoffs where auditStatus=2")
    List<ScientificPayoffs> findFailedScientificPayoffsList();

    @Query(value= "from ScientificPayoffs where auditStatus=0")
    List<ScientificPayoffs> findAllScientificPayoffsList();
}

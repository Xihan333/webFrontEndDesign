package org.fatmansoft.teach.repository.creativePratice;

import org.fatmansoft.teach.models.creativePractice.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipRepository extends JpaRepository<Internship,Integer> {
    @Query(value = "select max(internshipId) from Internship ")
    Integer getMaxId();

    @Query(value= "from Internship where student.studentId= ?1")
    List<Internship> findInternshipByStudentId(Integer studentId);

    Optional<Internship> findByInternshipId(Integer internshipId);

    @Query(value = "from Internship where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<Internship> findInternshipListByNumName(String numName);

    @Query(value= "from Internship where auditStatus=0")
    List<Internship> findWaitingInternshipList();

    @Query(value= "from Internship where auditStatus=1")
    List<Internship> findPassedInternshipList();

    @Query(value= "from Internship where auditStatus=2")
    List<Internship> findFailedInternshipList();

    @Query(value= "from Internship where auditStatus=0")
    List<Internship> findAllInternshipList();
}

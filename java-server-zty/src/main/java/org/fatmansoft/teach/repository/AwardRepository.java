package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Award;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AwardRepository extends JpaRepository<Award,Integer> {
    @Query(value = "select max(id) from Award  ")
    Integer getMaxId();

//    Optional<Award> findByAwardName(String AwardName);

    @Query(value = "from Award where ?1='' or awardName like %?1% or student.studentName like %?1% ")
    List<Award> findAwardListByName(String name);

    @Query(value = "from Award where student.studentNum=?2 and (?1='' or awardName like %?1% or student.studentName like %?1%) ")
    List<Award> findAwardListByNameForStudent(String name, String studentNum);
    @Query(value = "from Award where student.id=?1")
    List<Award> findAwardListByStudentId(Integer studentId);


}

package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade,Integer> {
        @Query(value = "select max(gradeId) from Grade")
        Integer getMaxId();
        Optional<Grade> findByGradeName(String gradeName);

        @Query(value = "from Grade where gradeId=?1")
        Optional<Grade> findByGradeId(Integer gradeId);
}

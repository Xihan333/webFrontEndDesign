package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Clazz;
        import org.fatmansoft.teach.models.Grade;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade,Integer> {
        @Query(value = "select max(gradeId) from Grade")
        Integer getMaxId();
        Optional<Grade> findByGradeName(String gradeName);
}
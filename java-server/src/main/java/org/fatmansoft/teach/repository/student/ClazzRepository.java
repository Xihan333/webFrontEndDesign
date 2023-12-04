package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz,Integer> {
    @Query(value = "select max(clazzId) from Clazz")
    Integer getMaxId();

    @Query(value = "from Clazz where grade.gradeName=?1 and clazzName=?2")
    Optional<Clazz> findByGradeNameAndClassName(String gradeName,String className);

    @Query(value = "from Clazz where grade.gradeId=?1")
    List<Clazz> findClazzListByGradeGradeId(Integer gradeId);

    @Query(value = "from Clazz where grade.gradeName=?1 and clazzName=?2 and campus.name=?3")
    Optional<Clazz> findByGradeNameAndClassNameAndCampusName(String gradeName, String className, String campusName);
}

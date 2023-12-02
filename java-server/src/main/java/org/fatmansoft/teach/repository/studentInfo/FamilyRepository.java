package org.fatmansoft.teach.repository.studentInfo;

import org.fatmansoft.teach.models.studentInfo.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyRepository extends JpaRepository<Family,Integer> {
    @Query(value = "select max(familyId) from Family ")
    Integer getMaxId();

    @Query(value = "from Family where ?1= student.studentId ")
    List<Family> findFamilyListByStudentId(Integer studentId);

    @Query(value = "from Family where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<Family> findFamilyListByNumName(String numName);
    @Query(value = "from Family where ?1= teacher.teacherId ")
    List<Family> findFamilyListByTeacherId(Integer teacherId);
}

package org.fatmansoft.teach.repository.studentInfo;

import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.fatmansoft.teach.models.studentInfo.EducationExperience;
import org.fatmansoft.teach.models.studentInfo.EducationExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationExperienceRepository extends JpaRepository<EducationExperience,Integer> {

    @Query(value = "select max(educationExperienceId) from EducationExperience ")
    Integer getMaxId();

    @Query(value = "from EducationExperience where ?1= student.studentId ")
    List<EducationExperience> findEducationExperienceListByStudentId(Integer id);

    @Query(value = "from EducationExperience where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<EducationExperience> findEducationExperienceListByNumName(String numName);

    @Query(value = "from EducationExperience where ?1= teacher.teacherId ")
    List<EducationExperience> findEducationExperienceListByTeacherId(Integer id);
}

package org.fatmansoft.teach.repository.creativePratice;

import org.fatmansoft.teach.models.creativePractice.NewProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewProjectRepository extends JpaRepository<NewProject,Integer> {
    @Query(value = "select max(newProjectId) from NewProject ")
    Integer getMaxId();

    @Query(value= "from NewProject where student.studentId= ?1")
    List<NewProject> findNewProjectByStudentId(Integer studentId);

    @Query(value= "from NewProject where teacher.teacherId= ?1")
    List<NewProject> findNewProjectByTeacherId(Integer teacherId);

    Optional<NewProject> findByNewProjectId(Integer newProjectId);

    @Query(value = "from NewProject where ?1='' or  teacher.person.num like %?1% or teacher.person.name like %?1% ")
    List<NewProject> findNewProjectListByTeacherNumName(String numName);
    @Query(value = "from NewProject where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<NewProject> findNewProjectListByStudentNumName(String numName);

    @Query(value= "from NewProject where auditStatus=0")
    List<NewProject> findWaitingNewProjectList();

    @Query(value= "from NewProject where auditStatus=1")
    List<NewProject> findPassedNewProjectList();

    @Query(value= "from NewProject where auditStatus=2")
    List<NewProject> findFailedNewProjectList();

    @Query(value= "from NewProject where auditStatus=0")
    List<NewProject> findAllNewProjectList();
}

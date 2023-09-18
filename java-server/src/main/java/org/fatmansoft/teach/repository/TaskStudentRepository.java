package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.TaskStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskStudentRepository extends JpaRepository<TaskStudent,Integer> {
    Optional<TaskStudent> findByStudentPersonNumAndTaskTaskId(String num, Integer taskId);
    Optional<TaskStudent> findByStudentStudentIdAndTaskTaskId(Integer studentId, Integer taskId);
    List<TaskStudent> findByTeamTeamId(Integer termId);
    List<TaskStudent> findByTaskTaskId(Integer taskId);
    @Query(value="select sum(weight) from TaskStudent where team.teamId=?1")
    Double getSumWight(Integer termId);

    @Query(value = "select team.teamId, student.person.name from TaskStudent where team.teamId is not null and task.taskId = ?1")
    List findTeamMemberNameList(Integer taskId);

    @Query(value = "from TaskStudent where team.teamId is not null and task.taskId = ?1 order by teachNo,student.className,student.person.num")
    List<TaskStudent> getStudentScoreList(Integer taskId);

    @Query(value = "select distinct t.task.course from TaskStudent t where t.student.person.num = ?1 order by t.task.course.num")
    List<Course> getCourseListOfStudent(String num);


}

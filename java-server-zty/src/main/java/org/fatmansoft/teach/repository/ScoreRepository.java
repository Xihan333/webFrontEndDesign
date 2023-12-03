package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Integer> {
    @Query(value = "select max(id) from Score  ")
    Integer getMaxId();
    List<Score> findByStudentId(Integer studentId);
    List<Score> findByCourseCourseNum(String courseNum);
    @Query(value="select s.course from Score s where s.student.id=?1")
    List<Course> findCourseList(Integer studentId);

    @Query(value="from Score  where course.courseName like %?1% or student.studentName like %?1%")
    List<Score> findScoreListByInput(String input);

    @Query(value="from Score  where student.studentNum = ?1")
    List<Score> findScoreListByStudentNum(String studentNum);

    @Query(value="select s.student from Score s where s.course.id=?1")
    List<Student> findStudentListByCourseId(Integer CourseId);

    @Query(value="from Score  where course.id = ?1")
    List<Score> findScoreListByCourseId(Integer CourseId);

    @Query(value="from Score  where ?1='' or student.studentNum like %?1% or student.studentName like %?1% or course.courseNum like %?1% or course.courseName like %?1%")
    List<Course> findCourseListByInput(String  input);

    @Query(value="from Score  where student.id = ?1 and( course.courseNum like %?2% or course.courseName like %?2%)")
    List<Course> findCourseListByInput(Integer studentId, String  input);//学生查找自己的某个课程

    @Query(value="from Score  where student.studentNum = ?1 and course.id = ?2")
    Optional<Score> findByStudentCourse(String studentNum, Integer courseId);

    @Query(value="from Score where student.studentNum = ?1 and course.sessions.week = ?2 and course.sessions.day = ?3")
    Optional<Score> findBySessions(String studentNum, Integer week, Integer day);
}

package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "select max(courseId) from Course")
    Integer getMaxId();

    Optional<Course> findByCourseId(Integer courseId);
    Optional<Course> findByNum(String courseNum);

    @Query(value = "select s.course from Score s where s.student.studentId=?1")
    List<Course> findCourseListByStudentId(Integer studentId);
    @Query(value = "select tc.course from TeacherCourse tc where tc.teacher.teacherId=?1")
    List<Course> findCourseListByTeacherId(Integer teacherId);

    @Query(value = "select s.course from Score s where s.student.studentId=?1 and (?2='' or s.course.num like %?2% or s.course.name like %?2%)")
    List<Course> findCourseListByStudentIdAndNumName(Integer studentId,String numName);
    @Query(value = "select c from TeacherCourse tc,Course c where tc.teacher.teacherId=?1 and tc.course.courseId=c.courseId and (?2='' or c.num like %?2% or c.name like %?2%)")
    List<Course> findCourseListByTeacherIdAndNumName(Integer teacherId,String numName);
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1%")
    List<Course> findCourseListByNumName(String numName);

    @Query(value = "from Course where grade.gradeId=?1")
    List<Course> findCourseListByGradeGradeId(Integer gradeId);

    @Query(value = "from Course where type=?1")
    List<Course> findCourseListByTypeId(Integer courseType);

    @Query(value = "select tc.course from TeacherCourse tc where tc.teacher.person.name=?1")
    List<Course> findCourseListByteacherName(Integer teacherName);

    @Query(value = "from Course where day=?1 and timeOrder=?2")
    List<Course> findCourseListByDayAndTimeOrder(Integer day, Integer timeOrder);

    @Query(value = "from Course where day=?1")
    List<Course> findCourseListByDay(Integer day);

    @Query(value = "from Course where timeOrder=?1")
    List<Course> findCourseListByTimeOrder(Integer timeOrder);

    @Query(value = "select s.course from Score s where s.student.studentId=?3 and s.course.day = ?1 and s.course.timeOrder = ?2")
    Optional<Course> findCourseListByStudentIdAndDayAndTimeOrder(Integer day, Integer timeOrder, Integer studentId);
}

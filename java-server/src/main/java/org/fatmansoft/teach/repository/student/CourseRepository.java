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

    @Query(value = "from Course where courseId=?1")
    Optional<Course> findByCourseId(Integer courseId);

    @Query(value = "from Course where num=?1")
    Optional<Course> findByNum(String courseNum);

    @Query(value = "select tc.course from TeacherCourse tc where tc.teacher.teacherId=?1")
    List<Course> findCourseListByTeacherId(Integer teacherId);

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

    @Query(value = "select tc.course from TeacherCourse tc where tc.teacher.teacherId=?2 and tc.course.courseId=?1")
    Optional<Course> findByCourseIdAndTeacherId(Integer courseId, Integer teacherId);
}

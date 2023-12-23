package org.fatmansoft.teach.repository.teacher;

import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse,Integer> {
    @Query(value = "select max(id) from TeacherCourse")
    Integer getMaxId();

    @Query(value = "from TeacherCourse where teacher.teacherId=?1 and course.courseId=?2")
    Optional<TeacherCourse> findByTeacherIdAndCourseId(Integer teacherId,Integer courseId);

    @Query(value = "from TeacherCourse where course.courseId=?1")
    List<TeacherCourse> findByCourseCourseId(Integer courseId);

    @Transactional
    @Modifying
    void deleteByCourseCourseId(Integer courseId);

    @Transactional
    @Modifying
    @Query(value = "delete from TeacherCourse where course.courseId=?1 and teacher.teacherId=?2")
    void deleteByCourseCourseIdAndTeacherTeacherId(Integer courseId,Integer teacherId);

    @Query(value = "from TeacherCourse where course.grade.gradeId=?1")
    List<TeacherCourse> findTeacherCourseListByGradeGradeId(Integer gradeId);

    @Query(value = "select s.teacherCourse from Score s where s.student.studentId=?1")
    List<TeacherCourse> findTeacherCourseListByStudentId(Integer studentId);

    @Query(value = "from TeacherCourse where teacher.teacherId=?1")
    List<TeacherCourse> findCourseListByTeacherId(Integer teacherId);

    @Query(value = "from TeacherCourse where ?1='' or course.num like %?1% or course.name like %?1%")
    List<TeacherCourse> findCourseListByCourseNumName(String numName);

    @Query(value = "from TeacherCourse where course.courseId=?1")
    List<TeacherCourse> findTeacherCourseListByCourseId(Integer courseId);

    @Query(value = "from TeacherCourse where course.type=?1")
    List<TeacherCourse> findCourseListByTypeId(Integer courseType);

    @Query(value = "from TeacherCourse where teacher.person.name=?1")
    List<TeacherCourse> findCourseListByteacherName(Integer teacherName);

    @Query(value = "from TeacherCourse where day=?1 and timeOrder=?2")
    List<TeacherCourse> findCourseListByDayAndTimeOrder(Integer day, Integer timeOrder);

    @Query(value = "from TeacherCourse where day=?1")
    List<TeacherCourse> findCourseListByDay(Integer day);

    @Query(value = "from TeacherCourse where timeOrder=?1")
    List<TeacherCourse> findCourseListByTimeOrder(Integer timeOrder);

    @Query(value = "select s.teacherCourse from Score s where s.student.studentId=?3 and s.teacherCourse.day=?1 and s.teacherCourse.timeOrder=?2 ")
    Optional<TeacherCourse> findCourseListByStudentIdAndDayAndTimeOrder(Integer day, Integer timeOrder, Integer studentId);

    @Query(value = "from TeacherCourse where id=?1")
    Optional<TeacherCourse> findByTeacherCourseId(Integer teacherCourseId);

    @Query(value = "from TeacherCourse where teacher.teacherId=?1 and day=?2 and timeOrder=?3 ")
    Optional<TeacherCourse> findByTeacherIdAmdDayAndTimeOrder(Integer teacherId, Integer day, Integer timeOrder);
}

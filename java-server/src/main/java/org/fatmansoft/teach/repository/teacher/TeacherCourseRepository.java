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
}

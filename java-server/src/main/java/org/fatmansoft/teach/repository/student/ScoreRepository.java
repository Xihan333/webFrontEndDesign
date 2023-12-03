package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Score 数据操作接口，主要实现Score数据的查询操作
 * List<Score> findByStudentStudentId(Integer studentId);  根据关联的Student的student_id查询获得List<Score>对象集合,  命名规范
 */

@Repository
public interface ScoreRepository extends JpaRepository<Score,Integer> {
    @Query(value = "select max(scoreId) from Score")
    Integer getMaxId();

    Optional<Score> findByStudentStudentIdAndCourseCourseId(Integer studentId,Integer courseId);
    List<Score> findByStudentStudentId(Integer studentId);
    List<Score> findByCourseCourseId(Integer courseId);

    @Query(value = "select s from TeacherCourse tc,Score s where tc.teacher.teacherId=?1 and tc.course.courseId=s.course.courseId")
    List<Score> findScoreListByTeacherId(Integer teacherId);
    @Query(value="from Score where (?1=0 or student.studentId=?1) and (?2=0 or course.courseId=?2) and (?3=0 or student.clazz.grade.gradeId=?3) and (?4=0 or student.clazz.clazzId=?4)")
    List<Score> findByStudentAndCourseAndGradeAndClazz(Integer studentId, Integer courseId,Integer gradeId,Integer clazzId);

    @Transactional
    @Modifying
    void deleteByCourseCourseId(Integer courseId);

    @Transactional
    @Modifying
    void deleteByStudentStudentId(Integer studentId);
}

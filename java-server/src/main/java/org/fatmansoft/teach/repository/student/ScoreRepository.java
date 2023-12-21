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

    List<Score> findByStudentStudentId(Integer studentId);

    @Query(value = "select s from TeacherCourse tc,Score s where tc.teacher.teacherId=?1 and tc.course.courseId=s.teacherCourse.course.courseId")
    List<Score> findScoreListByTeacherId(Integer teacherId);

    @Query(value = "from Score where (?1=0 or student.studentId=?1) and (?2=0 or teacherCourse.course.courseId=?2) and (?3=0 or student.clazz.grade.gradeId=?3) and (?4=0 or student.clazz.clazzId=?4)")
    List<Score> findByStudentAndCourseAndGradeAndClazz(Integer studentId, Integer courseId, Integer gradeId, Integer clazzId);

    @Query(value = "from Score where teacherCourse.course.courseId=?1")
    List<Score> findScoreListByCourseId(Integer courseId);

    @Query(value = "from Score where student.studentId=?1 and teacherCourse.course.courseId=?2 and teacherCourse.teacher.teacherId=?3")
    Optional<Score> findByStudentIdAndCourseIdAndTeacherId(Integer studentId, Integer courseId, Integer teacherId);

    @Query(value = "from Score where student.studentId=?1")
    List<Score> findScoreListByStudentId(Integer studentId);

    @Query(value = "from Score where teacherCourse.teacher.teacherId=?1")
    List<Score> findScoreListByTeachertId(Integer teacherId);

    @Query(value = "from Score where student.studentId=?1")
    List<Score> findStudentResultScores(Integer studentId);

    @Query(value = "from Score where teacherCourse.id=?1")
    List<Score> findScoreListByTeacherCourseId(Integer teacherCourseId);

    @Query(value = "from Score where student.studentId=?1 and teacherCourse.id=?2 and isResult")
    Optional<Score> findByStudentIdAndTeacherCourseId(Integer studentId, Integer teacherCourseId);
}

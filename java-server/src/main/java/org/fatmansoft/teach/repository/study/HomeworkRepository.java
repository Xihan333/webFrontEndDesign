package org.fatmansoft.teach.repository.study;

import org.fatmansoft.teach.models.study.Attendance;
import org.fatmansoft.teach.models.study.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Integer> {
    @Query(value = "select max(id) from Homework")
    Integer getMaxId();

    @Query(value = "from Homework where score.scoreId=?1")
    List<Homework> findHomeworkListByScoreScoreId(Integer scoreId);

    @Query(value = "from Homework where score.scoreId=?1 and name=?2")
    Optional<Homework> findHomeworkByScoreIdAndName(Integer scoreId, String Name);

    @Transactional
    @Modifying
    void deleteByScoreScoreId(Integer scoreId);

    @Query(value = "select h from TeacherCourse tc,Homework h where tc.teacher.teacherId=?1 and tc.course.courseId=h.score.course.courseId")
    List<Homework> findHomeworkListByTeacherId(Integer teacherId);

    @Query(value="from Homework where (?1=0 or score.student.clazz.grade.gradeId=?1) and (?2=0 or score.student.clazz.clazzId=?2) and (?3=0 or score.student.studentId=?3) and (?4=0 or score.course.courseId=?4) and (?5='' or name=?5)")
    List<Homework> findByGradeAndClazzAndStudentAndCourseAndHomework(Integer gradeId, Integer clazzId,Integer studentId,Integer courseId,String homeworkName);
}

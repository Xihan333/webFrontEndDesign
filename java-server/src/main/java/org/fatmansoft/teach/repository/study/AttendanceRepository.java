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
public interface AttendanceRepository extends JpaRepository<Attendance,Integer> {
    @Query(value = "select max(id) from Attendance")
    Integer getMaxId();

    @Query(value = "from Attendance where score.student.studentId=?1")
    List<Attendance> findByScoreStudentStudentId(Integer studentId);

    @Transactional
    @Modifying
    void deleteByScoreScoreId(Integer scoreId);

    @Query(value = "from Attendance where score.scoreId=?1")
    List<Attendance> findAttendanceListByScoreScoreId(Integer scoreId);
    @Query(value = "from Attendance where score.scoreId=?1 and date=?2")
    Optional<Attendance> findAttendanceByScoreIdAndDate(Integer scoreId, String Date);
    @Query(value = "select a from TeacherCourse tc,Attendance a where tc.teacher.teacherId=?1 and tc.course.courseId=a.score.course.courseId")
    List<Attendance> findAttendanceListByTeacherId(Integer teacherId);

    @Query(value="from Attendance where (?1=0 or score.student.clazz.grade.gradeId=?1) and (?2=0 or score.student.clazz.clazzId=?2) and (?3=0 or score.student.studentId=?3) and (?4=0 or score.course.courseId=?4) and (?5='' or date=?5)")
    List<Attendance> findByGradeAndClazzAndStudentAndCourseAndHomework(Integer gradeId, Integer clazzId, Integer studentId, Integer courseId, String date);
}

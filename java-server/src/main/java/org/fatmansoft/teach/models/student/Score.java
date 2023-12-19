package org.fatmansoft.teach.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fatmansoft.teach.models.teacher.TeacherCourse;

import javax.persistence.*;
/**
 * Score 成绩表实体类  保存成绩的的基本信息信息，
 * Integer scoreId 人员表 score 主键 score_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Course course 关联课程 course_id 关联课程的主键 course_id
 * Integer mark 成绩
 * Integer ranking 排名
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "score",
        uniqueConstraints = {
        })
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scoreId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    //初步打算让student维护关系

    @ManyToOne
    @JoinColumn(name = "teacher_course_id")
    private TeacherCourse teacherCourse;

    private Integer ranking;

    //平时分+期末考试分 = 总成绩（前端计算求和）

    private Integer commonMark; //平时成绩

    private Integer finalMark; //期末考试成级

    private Integer isResult; //成绩是否公布  0为公布 1为不公布

}

package org.fatmansoft.teach.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fatmansoft.teach.models.teacher.TeacherCourse;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "teacher_course_id")
    private TeacherCourse teacherCourse;

    //平时分+期末考试分 = 总成绩（前端计算求和）

    private Integer commonMark; //平时成绩

    private Integer finalMark; //期末考试成级

    private Integer isResult; //成绩是否公布  1为公布 0为不公布

}

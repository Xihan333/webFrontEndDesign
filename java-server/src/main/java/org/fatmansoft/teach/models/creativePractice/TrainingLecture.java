package org.fatmansoft.teach.models.creativePractice;


import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;

/**
 * 培训讲座
 */

@Getter
@Setter
@Entity
@Table(	name = "trainingLecture",
        uniqueConstraints = {
        })
public class TrainingLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer trainingLectureId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private Integer auditStatus = 0; //审核状态（审核中、已通过、未通过）

    private String date; // 时间

    private String location; // 地点

    private String speaker; // 主讲人

    private String theme; // 主题

    private Integer extendFraction; // 素拓分

    private String inspiration; // 收获与感

}

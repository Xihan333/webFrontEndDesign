package org.fatmansoft.teach.models.student;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.teacher.Teacher;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "achievement",
        uniqueConstraints = {
        })
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer achievementId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private String name;
    //奖项名称

    private String level;
    //等级 国家级 省级 市级 校级 院级

    private String type;

    private String content;
    //内容

    private String time;

    private Integer status=0;
    // 2为未通过 1为已通过审核 0为待审核
}

package org.fatmansoft.teach.models.creativePractice;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(	name = "competition",
        uniqueConstraints = {
        })
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer competitionId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private Integer auditStatus = 0; //审核状态（审核中、已通过、未通过）

    /**
     *参赛团队信息
     */

    private String groupName; // 团队名称

    private String member; //团队成员

    private String don; //指导教师

    private String awardStatus; // 获奖情况

    private String rank; // 排名

    /**
     *赛事信息
     */

    @Size(max = 50)
    private String title; // 比赛名称


    private String competitionLevel; //比赛级别（校级、省级、国家级）


}

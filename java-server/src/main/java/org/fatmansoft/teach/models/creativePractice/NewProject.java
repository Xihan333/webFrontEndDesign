package org.fatmansoft.teach.models.creativePractice;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.File;

/**
 * 创新项目
 */
@Getter
@Setter
@Entity
@Table(	name = "new_project",
        uniqueConstraints = {
        })
public class NewProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer newProjectId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private Integer auditStatus = 0; //审核状态（审核中、已通过、未通过）

    @Size(max = 20)
    private String groupName; // 团队名称

    private String member; //团队成员

    private String don; //指导教师

    @Size(max = 30)
    private String projectName; // 项目名称

    private String isContribute; // 是否投稿/参赛

    private String exposition; // 阐述及成果展示

}

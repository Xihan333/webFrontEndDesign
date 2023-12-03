package org.fatmansoft.teach.models.student;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(	name = "social",
        uniqueConstraints = {
        })
public class Social {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer socialId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private Integer auditStatus = 0; //审核状态（审核中0、已通过1、未通过2）
    private String day; // 时间

    private String isIndividual;  //个人立项或团队立项

    @Size(max = 20)
    private String groupName = "/";  // 团队名称 TODO：若选择个人立项则不需要填写此处

    @Size(max = 50)
    private String theme;  // 实践主题

    @Size(max = 200)
    private String digest; // 摘要

    private String selfAssessment; // 学生自评

    private String process; // 过程阐述


}

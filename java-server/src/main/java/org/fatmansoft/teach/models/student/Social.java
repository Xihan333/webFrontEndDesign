package org.fatmansoft.teach.models.student;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private String location;

    @Size(max = 20)
    private String groupName;  // 团队名称

    @Size(max = 50)
    private String theme;  // 实践主题

    @Size(max = 200)
    private String digest; // 摘要

    private String harvest; // 成果


}

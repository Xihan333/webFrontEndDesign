package org.fatmansoft.teach.models.student;

import lombok.*;
import org.fatmansoft.teach.models.teacher.Teacher;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @NotBlank
    @Size(max = 60)
    private String name;
    //奖项名称

    private String level;
    //等级 国家级 省级 市级 校级 院级

    @Size(max = 40)
    private String type;

    @Size(max = 200)
    private String content;
    //内容

    private String time;

    private Integer status=0;
    // 2为未通过 1为已通过审核 0为待审核

}

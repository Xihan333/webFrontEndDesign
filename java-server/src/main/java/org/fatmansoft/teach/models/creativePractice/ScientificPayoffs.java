package org.fatmansoft.teach.models.creativePractice;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;
import java.io.File;
/**
 * 科研成果
 */
@Getter
@Setter
@Entity
@Table(	name = "scientific_payoffs",
        uniqueConstraints = {
        })
public class ScientificPayoffs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scientificPayoffsId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private Integer auditStatus = 0; //审核状态（审核中、已通过、未通过）

    private String paperName;
    private String day; // 发布时间

    private String identity; // 该学生的身份

    private String firstAuthor; // 第一作者

    private String otherAuthor; // 其他作者

    private String correspondAuthor; // 通讯作者


    private String periodical; // 期刊


}

package org.fatmansoft.teach.models.creativePractice;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;
import java.io.File;

/**
 * 校外实习
 */
@Getter
@Setter
@Entity
@Table(	name = "internship",
        uniqueConstraints = {
        })
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer internshipId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private Integer auditStatus = 0; //审核状态（待审核0、已通过1、未通过2）

    private String unit; // 实习单位

    private String lastTime; // 实习时长

    private String post; // 实习岗位

    private String certificate; // 实习证明


}

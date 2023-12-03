package org.fatmansoft.teach.models.teacher;

import lombok.*;

import javax.persistence.*;
import java.io.File;
/**
 * 科研成果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "scientific_payoffs",
        uniqueConstraints = {
        })
public class ScientificPayoffs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scientificPayoffsId;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private Integer auditStatus = 0; //审核状态（审核中、已通过、未通过）

    private String paperName;

    private String day; // 发布时间

    private String identity; // 身份

    private String firstAuthor; // 第一作者

    private String otherAuthor; // 其他作者

    private String correspondAuthor; // 通讯作者

    private String periodical; // 期刊


}

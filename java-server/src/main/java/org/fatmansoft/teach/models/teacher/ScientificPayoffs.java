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

    private String paperName; //论文

    private String day; // 发布时间

    private String authors; //作者
}

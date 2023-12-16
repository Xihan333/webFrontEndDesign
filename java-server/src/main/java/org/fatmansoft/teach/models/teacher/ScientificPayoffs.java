package org.fatmansoft.teach.models.teacher;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @Size(max = 100)
    private String paperName; //论文


    private String day; // 发布时间

    @NotBlank
    @Size(max = 60)
    private String authors; //作者
}

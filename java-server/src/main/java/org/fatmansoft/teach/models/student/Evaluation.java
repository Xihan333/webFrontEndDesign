package org.fatmansoft.teach.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "evaluation",
        uniqueConstraints = {
        })
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer evaluationId;

    @ManyToOne
    @JoinColumn(name = "evaluator_id")
    private Student evaluator; //评估者

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student; //被评估者

    @NotBlank
    @Size(max = 200)
    private String eval; //评价内容

    @NotBlank
    private String evalTime; //评价时间

}

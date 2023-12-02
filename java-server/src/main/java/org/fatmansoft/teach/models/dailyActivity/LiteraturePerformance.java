package org.fatmansoft.teach.models.dailyActivity;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;

import javax.persistence.*;
import javax.validation.Valid;

@Getter
@Setter
@Entity
@Table(	name = "LiteraturePerformance",
        uniqueConstraints = {
        })

public class LiteraturePerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer performanceId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;


    private String day;//汇演日期

    private String location;//汇演地点

    private String performanceType;//汇演类型

    private String programme;//节目


}

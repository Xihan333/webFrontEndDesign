package org.fatmansoft.teach.models.dailyActivity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;

@Getter
@Setter
@Entity
@Table(	name = "Travel",
        uniqueConstraints = {
        })

public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer travelId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String day;//旅行日期

    private String introduction;//旅行介绍

    private String title;//出游主题
    private String location;//出游地点

}

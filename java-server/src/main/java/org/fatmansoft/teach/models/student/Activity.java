package org.fatmansoft.teach.models.student;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "activity",
        uniqueConstraints = {
        })

public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String day;//活动日期

    private String introduction;//活动介绍

    private String location;//活动地点

    private String title;//活动主题

}

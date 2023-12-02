package org.fatmansoft.teach.models.dailyActivity;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "sportActivity",
        uniqueConstraints = {
        })

public class SportActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String day;//活动日期

    private String introduction;//活动介绍

    private String location;//体育活动地点

    private String prize;//获奖情况

    private String title;//活动主题

}

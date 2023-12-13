package org.fatmansoft.teach.models.student;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Size(max = 200)
    private String introduction;//活动介绍

    @Size(max = 60)
    private String location;//活动地点

    @NotBlank
    @Size(max = 30)
    private String title;//活动主题

}

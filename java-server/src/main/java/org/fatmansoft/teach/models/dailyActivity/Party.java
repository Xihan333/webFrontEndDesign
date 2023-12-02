package org.fatmansoft.teach.models.dailyActivity;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "Party",
        uniqueConstraints = {
        })

public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partyId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

/*
聚会信息
 */
    private String day;//聚会日期

    private String introduction;//聚会介绍，内容

    private String location;//聚会地点

    private String organizer;//组织者

    private String title; //聚会主题

}

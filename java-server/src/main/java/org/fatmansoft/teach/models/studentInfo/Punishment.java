package org.fatmansoft.teach.models.studentInfo;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "punishment",
        uniqueConstraints = {
        })
public class Punishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer punishmentId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String name;

    private String content;

    private String time;

}

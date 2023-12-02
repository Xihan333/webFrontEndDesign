package org.fatmansoft.teach.models.studentInfo;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(	name = "family",
        uniqueConstraints = {
        })
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer familyId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Size(max = 20)
    private String name;

    private String gender = "1";

    private String birthday;

    private String relation;

    private String description;

}

package org.fatmansoft.teach.models.studentInfo;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "education_experience",
        uniqueConstraints = {
        })
public class EducationExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer educationExperienceId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    private String startTime;

    private String endTime;

    private String name;//学校名称

    private String level;//等级

    private String proofTeacher;

    private String description;

    private String position;//职位

}

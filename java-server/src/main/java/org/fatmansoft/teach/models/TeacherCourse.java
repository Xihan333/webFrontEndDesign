package org.fatmansoft.teach.models;


import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.study.Course;

import javax.persistence.*;

@Entity
@Table(	name = "teacher_course",
        uniqueConstraints = {
        })
@Getter
@Setter
public class TeacherCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    //初步打算让course维护关系
}

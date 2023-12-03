package org.fatmansoft.teach.models.teacher;


import lombok.*;
import org.fatmansoft.teach.models.student.Course;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "teacher_course",
        uniqueConstraints = {
        })
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

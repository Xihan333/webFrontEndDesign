package org.fatmansoft.teach.models.teacher;


import lombok.*;
import org.fatmansoft.teach.models.student.Course;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @NotBlank
    private Integer selectedCount = 0; //选课人数

    @NotBlank
    private Integer courseCapacity; //课容量

    //上课时间
    @NotBlank
    private Integer day; //星期x   1 2 3 4 5 6 7

    @NotBlank
    private Integer timeOrder; //第x节 1 2 3 4 5

    @NotBlank
    private String place; //上课地点
}

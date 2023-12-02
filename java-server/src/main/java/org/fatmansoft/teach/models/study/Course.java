package org.fatmansoft.teach.models.study;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.Grade;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */
@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String num;

    private Integer hour;

    private Integer credit;//学分

    private String time;

    private String place;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;
}

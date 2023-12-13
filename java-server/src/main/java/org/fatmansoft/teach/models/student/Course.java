package org.fatmansoft.teach.models.student;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String num; //课序号

    @NotBlank
    private Integer hour; //总课时

    @NotBlank
    private Integer credit; //学分

    @NotBlank
    private Integer type; //必修 限选 任选  0 1 2 type

    @OneToOne
    @JoinColumn(name="capmus_id")
    private Campus campus; //学院

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade; //开课年级

    private String introduction; //课程介绍

}

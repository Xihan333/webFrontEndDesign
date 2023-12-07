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

    private Integer hour; //总课时

    private Integer credit; //学分

//    //上课时间
//    private Integer day; //星期x   1 2 3 4 5 6 7
//
//    private Integer timeOrder; //第x节 1 2 3 4 5
//
//    private String place; //上课地点

    private Integer type; //必修 限选 任选  0 1 2 type

    @OneToOne
    @JoinColumn(name="capmus_id")
    private Campus campus; //学院

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade; //开课年级

//    private Integer selectedCount = 0; //选课人数
//
//    private Integer courseCapacity; //课容量

    private String introduction; //课程介绍



}

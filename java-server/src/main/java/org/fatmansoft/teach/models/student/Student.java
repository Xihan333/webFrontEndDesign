package org.fatmansoft.teach.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fatmansoft.teach.models.system.Person;

import javax.persistence.*;

/**
 * Student学生表实体类 保存每个学生的信息，
 * Integer studentId 用户表 student 主键 student_id
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String class 班级
 *
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "student",
        uniqueConstraints = {
        })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz clazz;

}

package org.fatmansoft.teach.models.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
/**
 * Person人员表实体类 保存人员的基本信息信息， 账户、学生和教师都关联人员，
 * Integer personId 人员表 person 主键 person_id
 * String num 人员编号
 * String name 人员名称
 * String type 人员类型  0管理员 1学生 2教师
 * String dept 学院
 * String card 身份证号
 * String gender 性别  1 男 2 女
 * String birthday 出生日期
 * String email 邮箱
 * String phone 电话
 * String address 地址
 * String introduce 个人简介
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "person",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "num"),   //人员表中的编号 唯一
        })
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personId;

    @NotBlank    // 字段非空
    @Size(max = 20)   //字段长度最长为20
    private String num;

    @Size(max = 50)
    private String name;

    @Size(max = 2)
    private String type;

    @Size(max = 50)
    private String dept;

    @Size(max = 20)
    private String card;
    @Size(max = 2)
    private String gender;//性别

    private String birthday;

    @Size(max = 60)
    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String address;

    @Size(max = 1000)
    private String introduce;

}

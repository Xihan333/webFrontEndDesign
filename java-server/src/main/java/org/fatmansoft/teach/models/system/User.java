package org.fatmansoft.teach.models.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
/**
 * User用户表实体类 保存每个允许登录的信息人员的账号信息，
 * Integer userId 用户表 user 主键 user_id
 * UserType userType 关联到用户类型对象
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String userName 登录账号 和Person 中的num属性相同
 * String password 用户密码 非对称加密，这能加密，无法解码
 *
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userName"),
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @ManyToOne()
    @JoinColumn(name = "user_type_id")
    private UserType userType;

    @OneToOne
    @JoinColumn(name="person_id")
    private Person person;

    @NotBlank
    @Size(max = 20)
    private String userName;

    @NotBlank
    @Size(max = 60)
    private String password;

    private Integer loginCount;

    @Size(max = 20)
    private String lastLoginTime;

    @Size(max = 20)
    private String createTime;

    private Integer creatorId;

}

package org.fatmansoft.teach.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "blog",
        uniqueConstraints = {
        })
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer blogId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Size(max = 50)
    private String title; //题目

    @Size(max = 15)
    private String tag; //标签

    private String createTime; //创建时间（即发布时间）

    private String updateTime; // 设定是作者可以在任意时间修改

    private String content; //文章内容

    private String digest; //摘要
}

package org.fatmansoft.teach.models.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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

    private String text;

    private String title;

    private Integer praise;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;// 设定是作者可以在任意时间修改
}

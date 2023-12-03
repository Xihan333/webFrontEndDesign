package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "award",
        uniqueConstraints = {
        })
public class Award {
    @Id
    private Integer id;
    @NotBlank
    @Size(max = 50)
    private String awardName;//奖励名称

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;//获奖学生

    private Date awardTime;

    private String info;//奖励描述

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getAwardTime() {
        return awardTime;
    }

    public void setAwardTime(Date awardTime) {
        this.awardTime = awardTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
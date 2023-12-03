package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "paper",
        uniqueConstraints = {
        })
public class Paper {//论文著作
    @Id
    private Integer id;
    @NotBlank

    private String PaperName;//论文名字

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;//所属老师

    private Date publishTime;

    private String info;//论文简介

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaperName() {
        return PaperName;
    }

    public void setPaperName(String paperName) {
        PaperName = paperName;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
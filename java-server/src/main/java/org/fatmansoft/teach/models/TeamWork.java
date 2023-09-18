package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "team_work",
        uniqueConstraints = {
        })
public class TeamWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workId;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private Integer courseId;
    private Integer week;

    @Size(max = 300)
    private String content;
    @Size(max = 20)
    private String modifyTime;

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}

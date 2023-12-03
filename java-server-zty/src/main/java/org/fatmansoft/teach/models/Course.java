package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
public class Course {
    @Id
    private Integer id;
    @NotBlank
    @Size(max = 20)
    private String courseNum;//课程号

    @Size(max = 50)
    private String courseName;//名字
    private Integer credit;//学分

    @Size(max = 50)
    private String preCourse;//先修课


    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "sessionsId")
    private Sessions sessions;

    private String location;//上课地点

    private String info;//课程简介

    private Integer numOfStudent;//选课人数
    private Integer maxNumOfStudent;//最大选课人数
    private Date deadline;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public String getPreCourse() {
        return preCourse;
    }

    public void setPreCourse(String preCourse) {
        this.preCourse = preCourse;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getNumOfStudent() {
        return numOfStudent;
    }

    public void setNumOfStudent(Integer numOfStudent) {
        this.numOfStudent = numOfStudent;
    }

    public Integer getMaxNumOfStudent() {
        return maxNumOfStudent;
    }

    public void setMaxNumOfStudent(Integer maxNumOfStudent) {
        this.maxNumOfStudent = maxNumOfStudent;
    }

    public Sessions getSessions() {
        return sessions;
    }

    public void setSessions(Sessions sessions) {
        this.sessions = sessions;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}

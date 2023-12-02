package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "practice",
        uniqueConstraints = {
        })
public class Practice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer PracticeId;

    private Integer studentId;

    private String day;

    private String introduction;

    private String location;

    private Integer practiceType;

    public Integer getPracticeId() {
        return PracticeId;
    }

    public void setPracticeId(Integer practiceId) {
        PracticeId = practiceId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPracticeType() {
        return practiceType;
    }

    public void setPracticeType(Integer practiceType) {
        this.practiceType = practiceType;
    }
}

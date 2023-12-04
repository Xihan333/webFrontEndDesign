package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "socialPractice",
        uniqueConstraints = {
        })
public class SocialPractice {
    @Id
    private Integer id;
    @NotBlank
    @Size(max = 50)
    private String socialPracticeName;//奖励名称

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;//实践学生

    private Date socialPracticeTime;

    private String address;
    private String content;//内容

    private String outcome; // 成果

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSocialPracticeName() {
        return socialPracticeName;
    }

    public void setSocialPracticeName(String socialPracticeName) {
        this.socialPracticeName = socialPracticeName;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getSocialPracticeTime() {
        return socialPracticeTime;
    }

    public void setSocialPracticeTime(Date socialPracticeTime) {
        this.socialPracticeTime = socialPracticeTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
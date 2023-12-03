package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "family_member",
        uniqueConstraints = {
        })
public class FamilyMember {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    private String name;
    private String sex;
    private String rel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}

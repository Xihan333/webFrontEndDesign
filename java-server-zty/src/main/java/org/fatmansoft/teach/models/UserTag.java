package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "user_tag",
        uniqueConstraints = {
        })
public class UserTag {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "tagId")
    private Tag tag;

    @ManyToOne
    @JoinColumn(name = "taggerId")
    private Student tagger;


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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Student getTagger() {
        return tagger;
    }

    public void setTagger(Student tagger) {
        this.tagger = tagger;
    }
}
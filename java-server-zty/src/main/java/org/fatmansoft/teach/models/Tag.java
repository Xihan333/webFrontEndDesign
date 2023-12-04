package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "tag",
        uniqueConstraints = {
        })
public class Tag {
    @Id
    private Integer id;

    private String tag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
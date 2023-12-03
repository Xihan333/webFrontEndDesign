package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "info"
)
public class Info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max=20)
    private String info;
    @Size(max=20)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

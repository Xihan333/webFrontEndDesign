package org.fatmansoft.teach.models;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(	name = "clazz",
        uniqueConstraints = {
        })
@Getter
@Setter
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clazzId;

    private String clazzName;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;
}

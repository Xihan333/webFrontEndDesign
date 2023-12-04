package org.fatmansoft.teach.models.student;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(	name = "grade",
        uniqueConstraints = {
        })
@Getter
@Setter
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gradeId;

    private String gradeName;
}

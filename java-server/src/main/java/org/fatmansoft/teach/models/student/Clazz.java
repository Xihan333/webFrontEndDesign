package org.fatmansoft.teach.models.student;


import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "clazz",
        uniqueConstraints = {
        })
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer clazzId;

    private String clazzName;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;
}

package org.fatmansoft.teach.models.student;


import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "grade",
        uniqueConstraints = {
        })
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gradeId;

    private String gradeName;
}

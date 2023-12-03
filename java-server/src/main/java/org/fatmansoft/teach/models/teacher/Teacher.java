package org.fatmansoft.teach.models.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fatmansoft.teach.models.system.Person;

import javax.persistence.*;
import javax.validation.constraints.Size;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(	name = "teacher",
            uniqueConstraints = {
            })
    public class Teacher {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer teacherId;

        @OneToOne
        @JoinColumn(name="person_id")
        private Person person;

        @Size(max = 20)
        private String title;

        @Size(max = 50)
        private String degree;

        @Size(max = 100)
        private String direction; //研究方向

    }

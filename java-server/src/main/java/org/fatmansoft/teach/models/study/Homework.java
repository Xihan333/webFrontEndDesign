package org.fatmansoft.teach.models.study;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(	name = "homework",
        uniqueConstraints = {
        })
public class Homework {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "score_id")
        private Score score;

        private String name;

        private String mark;
}

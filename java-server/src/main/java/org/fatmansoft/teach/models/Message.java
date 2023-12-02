package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;
import org.fatmansoft.teach.models.system.Person;

import javax.persistence.*;

    @Getter
    @Setter
    @Entity
    @Table(name = "message")
    public class Message {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer messageId;

        @ManyToOne
        @JoinColumn(name="student_id")
        private Student student;

        private String place;
        private String start;
        private String end;
        private Integer status;
        // 2为未通过 1为已通过审核 0为待审核
    }



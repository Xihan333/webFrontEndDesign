package org.fatmansoft.teach.models;

import org.fatmansoft.teach.models.system.Person;

import javax.persistence.*;
import javax.validation.constraints.Size;

    @Entity
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

        public Integer getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Integer teacherId) {
            this.teacherId = teacherId;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }
    }

package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "task_student",
        uniqueConstraints = {
        })
public class TaskStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskStudentId;
    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team;
    @Size(max = 4)
    private String teachNo;
    private Double  weight;
    private Double score;
    @Size(max = 4)
    private String level;

    @ManyToOne
    @JoinColumn(name="task_id")
    private Task task;

    public Integer getTaskStudentId() {
        return taskStudentId;
    }

    public void setTaskStudentId(Integer taskStudentId) {
        this.taskStudentId = taskStudentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getTeachNo() {
        return teachNo;
    }

    public void setTeachNo(String teachNo) {
        this.teachNo = teachNo;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}

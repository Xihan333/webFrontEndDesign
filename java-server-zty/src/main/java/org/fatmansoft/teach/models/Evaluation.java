package org.fatmansoft.teach.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(	name = "evaluation",
        uniqueConstraints = {
        })
public class Evaluation {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "evaluatorId")
    private Student evaluator;//评估者

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;//被评估者
    private String eval;//评价内容
    private Date evalTime;//评价时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Student evaluator) {
        this.evaluator = evaluator;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getEval() {
        return eval;
    }

    public void setEval(String eval) {
        this.eval = eval;
    }

    public Date getEvalTime() {
        return evalTime;
    }

    public void setEvalTime(Date evalTime) {
        this.evalTime = evalTime;
    }

}
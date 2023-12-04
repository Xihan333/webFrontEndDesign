package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "sessions",
        uniqueConstraints = {
        })
public class Sessions {
    @Id
    private Integer id;
    //上课时间
    //类比标签功能，约定好每一个整形数字代表的时间。
    //十位代表周几，个位代表第几节课。

    private Integer week;
    private Integer day;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
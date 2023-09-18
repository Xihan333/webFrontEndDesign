package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "statistics_day",
        uniqueConstraints = {
        })
public class StatisticsDay {
    @Id
    private String  day;
    private Integer loginCount;
    private Integer requestCount;
    private Integer createCount;
    private Integer modifyCount;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getCreateCount() {
        return createCount;
    }

    public void setCreateCount(Integer createCount) {
        this.createCount = createCount;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getModifyCount() {
        return modifyCount;
    }

    public void setModifyCount(Integer modifyCount) {
        this.modifyCount = modifyCount;
    }
}

package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "request_log",
        uniqueConstraints = {
        })
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 100)
    private String url;
    @Size(max = 20)
    private String username;
    @Size(max = 20)
    private String startTime;
    private Double requestTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Double requestTime) {
        this.requestTime = requestTime;
    }

}

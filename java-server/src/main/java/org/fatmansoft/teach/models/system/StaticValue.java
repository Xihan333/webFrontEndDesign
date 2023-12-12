package org.fatmansoft.teach.models.system;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(	name = "static_value",
        uniqueConstraints = {
        })
public class StaticValue implements Serializable {

    @Id
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "comment")
    private String comment;

    private static final long serialVersionUID = 1L;
}

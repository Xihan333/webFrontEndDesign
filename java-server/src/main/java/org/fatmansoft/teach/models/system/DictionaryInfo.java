package org.fatmansoft.teach.models.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
/**
 * DictionaryInfo 课程表实体类  保存数据字典的的基本信息信息， 数据库表名 dictionary
 * Integer id 数据字典表  dictionary 主键 id
 * Integer pid  父节点ID  所属于的字典类型的id
 * String value 字典值
 * String label 字典名
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "dictionary",
        uniqueConstraints = {
        })
public class DictionaryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer pid;

    @Size(max = 40)
    private String value;

    @Size(max = 40)
    private String label;
}

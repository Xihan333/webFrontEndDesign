package org.fatmansoft.teach.payload.response;

/**
 * OptionItem 选项数据类
 * Integer id  数据项id
 * String value 数据项值
 * String label 数据值标题
 */
public class OptionItem {
    private Integer id;
    private String value;
    private String title;
    public OptionItem(){

    }
    public OptionItem(Integer id, String value, String title){
        this.id = id;
        this.value = value;
        this.title = title;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString(){
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package org.fatmansoft.teach.payload.response;

import org.fatmansoft.teach.util.CommonMethod;

import java.util.List;

/**
 * MyTreeNode  树节点类
 * Integer id 节点ID 菜单或数据字典的主键ID
 * String value; 节点值
 * String label  节点标题
 * Integer pid 父节点
 * List<MyTreeNode> childList 子节点对象列表
 */
public class MyTreeNode {
    private Integer id;
    private String value;
    private String label;
    private String title;
    private Integer pid;
    private Integer isLeaf;
    private String userTypeIds;
    private String parentTitle;
    private List<MyTreeNode> children;
    public MyTreeNode(){

    }
    public MyTreeNode(Integer id, String value, String title,Integer isLeaf){
        this.id  = id;
        this.value = value;
        this.title = title;
        this.isLeaf = isLeaf;
//        this.label = id+"-" + title;
    }
    public String toString(){
        return label;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public List<MyTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<MyTreeNode> children) {
        this.children = children;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserTypeIds() {
        return userTypeIds;
    }

    public void setUserTypeIds(String userTypeIds) {
        this.userTypeIds = userTypeIds;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }
}

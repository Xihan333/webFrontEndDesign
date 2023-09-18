package org.fatmansoft.teach.util;

import org.fatmansoft.teach.payload.response.OptionItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComDataUtil 内存缓冲存储数据示例程序
 * Map<String, List<OptionItem>> dictListMap 数据字典列表Map集合
 * Map<String, Map<String, String>> dictMapMap 数据字典值名映射Map对象集合;
 * Map<Integer, String> htmlMap html映射Map对象集合
 */
public class ComDataUtil {
    private Map<String, List<OptionItem>> dictListMap = new HashMap<String, List<OptionItem>>();
    private Map<String, Map<String, String>> dictMapMap = new HashMap<String, Map<String, String>>();
    private Map<String,String> systemMap = new HashMap();
    private Map<Integer, String> htmlMap = new HashMap();
    private int htmlCount= 0;

    private static ComDataUtil instance = new ComDataUtil();

    public static ComDataUtil getInstance(){
        return instance;
    }

    public Map<String, List<OptionItem>> getDictListMap() {
        return dictListMap;
    }

    public void setDictListMap(Map<String, List<OptionItem>> dictListMap) {
        this.dictListMap = dictListMap;
    }

    public Map<String, Map<String, String>> getDictMapMap() {
        return dictMapMap;
    }

    public void setDictMapMap(Map<String, Map<String, String>> dictMapMap) {
        this.dictMapMap = dictMapMap;
    }
    public void setSystemMap(Map<String, String> systemMap) {
        this.systemMap = systemMap;
    }

    public List<OptionItem> getDictionaryOptionItemList(String code) {
        return dictListMap.get(code);
    }
    public String getDictionaryLabelByValue(String code,String value) {
        if(value == null || value.length()==0)
            return "";
        Map<String, String> m = dictMapMap.get(code);
        if(m == null)
            return "";
        String label = m.get(value);
        if(label == null)
            return "";
        else
            return label;
    }

    public synchronized int  addHtmlString(String html) {
        htmlCount++;
        htmlMap.put(htmlCount,html);
        return htmlCount;
    }
    public synchronized String  getHtmlString(int htmlCount) {
        return htmlMap.get(htmlCount);
    }

    public boolean teamCanApply(){
        String str = systemMap.get("teamCanApply");
        if(str !=null && str.equals("true")) {
            return true;
        }else {
            return false;
        }
    }
    public String getTeamNumPrefix(){
        return systemMap.get("teamNumPrefix");
    }
    public boolean teamLock(){
        String str = systemMap.get("teamLock");
        if(str !=null && str.equals("true")) {
            return true;
        }else {
            return false;
        }
    }
    public boolean teamView(){
        String str = systemMap.get("teamView");
        if(str !=null && str.equals("true")) {
            return true;
        }else {
            return false;
        }
    }
    public Integer currentTaskId(){
        String str = systemMap.get("currentTaskId");
        if(str !=null && str.length() > 0) {
            return Integer.parseInt(str);
        }else {
            return null;
        }
    }
    public String currentPath(){
        return systemMap.get("currentPath");
    }
}

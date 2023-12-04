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

    // 2为未通过 1为已通过审核 0为待审核
    public String getStatusByValue(Integer status){
        if(status==0){
            return "待审核";
        }else if(status==1){
            return "已通过";
        }else{
            return "未通过";
        }
    }


    public synchronized int  addHtmlString(String html) {
        htmlCount++;
        htmlMap.put(htmlCount,html);
        return htmlCount;
    }
    public synchronized String  getHtmlString(int htmlCount) {
        return htmlMap.get(htmlCount);
    }

}

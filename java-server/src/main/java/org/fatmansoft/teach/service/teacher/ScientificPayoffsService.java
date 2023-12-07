package org.fatmansoft.teach.service.teacher;


import org.fatmansoft.teach.models.teacher.ScientificPayoffs;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.repository.teacher.ScientificPayoffsRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScientificPayoffsService {
    @Autowired
    private ScientificPayoffsRepository scientificPayoffsRepository;

    public Map getMapFromScientificPayoffs(ScientificPayoffs scientificPayoffs) {
        Map m = new HashMap();
        Teacher t;
        if (scientificPayoffs == null)
            return m;
        m.put("scientificPayoffsId",scientificPayoffs.getScientificPayoffsId());
        m.put("day",scientificPayoffs.getDay());
        m.put("authors",scientificPayoffs.getAuthors());
        m.put("paperName",scientificPayoffs.getPaperName());
        t = scientificPayoffs.getTeacher();
        if (t ==null)
            return m;
       else{
            m.put("personId", t.getPerson().getPersonId());
            m.put("num", t.getPerson().getNum());
            m.put("name", t.getPerson().getName());
        }
        return m;
    }

    /**
     * getScientificPayoffsMapList 根据输入参数查询得到学生科研成果数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getScientificPayoffsMapList(String numName) {
        List dataList = new ArrayList();
        List<ScientificPayoffs> tList = scientificPayoffsRepository.findScientificPayoffsListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(tList.get(i)));
        }
        return dataList;
    }

    public List getScientificPayoffsMapListByTeacherId(Integer teacherId) {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findScientificPayoffsByTeacherId(teacherId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
        }
        return dataList;
    }
}

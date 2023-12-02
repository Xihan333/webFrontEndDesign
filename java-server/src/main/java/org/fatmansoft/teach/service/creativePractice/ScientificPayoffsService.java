package org.fatmansoft.teach.service.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.ScientificPayoffs;
import org.fatmansoft.teach.repository.creativePratice.ScientificPayoffsRepository;
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
        Student s;
        Teacher t;
        if (scientificPayoffs == null)
            return m;
        m.put("scientificPayoffsId",scientificPayoffs.getScientificPayoffsId());
        m.put("day",scientificPayoffs.getDay());
        m.put("identity",scientificPayoffs.getIdentity());
        m.put("firstAuthor",scientificPayoffs.getFirstAuthor());
        m.put("otherAuthor",scientificPayoffs.getOtherAuthor());
        m.put("correspondAuthor",scientificPayoffs.getCorrespondAuthor());
        m.put("auditStatus", scientificPayoffs.getAuditStatus());
        m.put("periodical",scientificPayoffs.getPeriodical());
        m.put("paperName",scientificPayoffs.getPaperName());
        Integer status = scientificPayoffs.getAuditStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));
        s = scientificPayoffs.getStudent();
        t = scientificPayoffs.getTeacher();
        if (s == null && t ==null)
            return m;
        if (s != null) {
            m.put("personId", s.getPerson().getPersonId());
            m.put("num", s.getPerson().getNum());
            m.put("name", s.getPerson().getName());
            m.put("dept", s.getPerson().getDept());
            m.put("card", s.getPerson().getCard());
            String gender = s.getPerson().getGender();
            m.put("gender", s.getPerson().getGender());
            m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
            m.put("birthday", s.getPerson().getBirthday());  //时间格式转换字符串
            m.put("email", s.getPerson().getEmail());
            m.put("phone", s.getPerson().getPhone());
            m.put("address", s.getPerson().getAddress());
            m.put("introduce", s.getPerson().getIntroduce());
        }else{
            m.put("personId", t.getPerson().getPersonId());
            m.put("num", t.getPerson().getNum());
            m.put("name", t.getPerson().getName());
            m.put("dept", t.getPerson().getDept());
            m.put("card", t.getPerson().getCard());
            String gender = t.getPerson().getGender();
            m.put("gender", t.getPerson().getGender());
            m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
            m.put("birthday", t.getPerson().getBirthday());  //时间格式转换字符串
            m.put("email", t.getPerson().getEmail());
            m.put("phone", t.getPerson().getPhone());
            m.put("address", t.getPerson().getAddress());
            m.put("introduce", t.getPerson().getIntroduce());
        }
        return m;
    }

    /**
     * getScientificPayoffsMapList 根据输入参数查询得到学生科研成果数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getScientificPayoffsMapList(String numName) {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findScientificPayoffsListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
        }
        List<ScientificPayoffs> tList = scientificPayoffsRepository.findScientificPayoffsListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(tList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询所有科研成果 按编号排序
     */
    public List getAllScientificPayoffsMapList() {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findAllScientificPayoffsList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询审核中状态科研成果 按编号排序
     */
    public List getWaitingScientificPayoffsMapList() {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findWaitingScientificPayoffsList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询已通过状态科研成果 按编号排序
     */
    public List getPassedScientificPayoffsMapList() {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findPassedScientificPayoffsList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询未通过状态科研成果 按编号排序
     */
    public List getFailedScientificPayoffsMapList() {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findFailedScientificPayoffsList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
        }
        return dataList;
    }
    public List getScientificPayoffsMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<ScientificPayoffs> sList = scientificPayoffsRepository.findScientificPayoffsByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromScientificPayoffs(sList.get(i)));
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

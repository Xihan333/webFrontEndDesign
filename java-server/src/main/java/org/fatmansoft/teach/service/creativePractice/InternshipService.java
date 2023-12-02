package org.fatmansoft.teach.service.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.creativePractice.Internship;
import org.fatmansoft.teach.repository.creativePratice.InternshipRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;

    public Map getMapFromInternship(Internship internship) {
        Map m = new HashMap();
        Student s;
        if (internship == null)
            return m;
        m.put("internshipId",internship.getInternshipId());
        m.put("unit",internship.getUnit());
        m.put("lastTime",internship.getLastTime());
        m.put("post",internship.getPost());
        m.put("certificate",internship.getCertificate());
        m.put("auditStatus", internship.getAuditStatus());
        Integer status = internship.getAuditStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));
        s = internship.getStudent();
        if (s == null)
            return m;
        m.put("studentId", s.getStudentId());
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
        return m;
    }

    /**
     * getInternshipMapList 根据输入参数查询得到学生校外实习数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getInternshipMapList(String numName) {
        List dataList = new ArrayList();
        List<Internship> sList = internshipRepository.findInternshipListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromInternship(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询所有校外实习 按编号排序
     */
    public List getAllInternshipMapList() {
        List dataList = new ArrayList();
        List<Internship> sList = internshipRepository.findAllInternshipList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromInternship(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询审核中状态校外实习 按编号排序
     */
    public List getWaitingInternshipMapList() {
        List dataList = new ArrayList();
        List<Internship> sList = internshipRepository.findWaitingInternshipList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromInternship(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询已通过状态校外实习 按编号排序
     */
    public List getPassedInternshipMapList() {
        List dataList = new ArrayList();
        List<Internship> sList = internshipRepository.findPassedInternshipList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromInternship(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询未通过状态校外实习 按编号排序
     */
    public List getFailedInternshipMapList() {
        List dataList = new ArrayList();
        List<Internship> sList = internshipRepository.findFailedInternshipList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromInternship(sList.get(i)));
        }
        return dataList;
    }
    public List getInternshipMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Internship> sList = internshipRepository.findInternshipByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromInternship(sList.get(i)));
        }
        return dataList;
    }
}

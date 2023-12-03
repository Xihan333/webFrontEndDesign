package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Social;
import org.fatmansoft.teach.repository.student.SocialRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SocialService {
    @Autowired
    private SocialRepository socialRepository;

    public Map getMapFromSocial(Social social) {
        Map m = new HashMap();
        Student s;
        if (social == null)
            return m;
        m.put("socialId",social.getSocialId());
        m.put("day", social.getDay());
        m.put("isIndividual",social.getIsIndividual());
        String isIndividual = social.getIsIndividual();
//        m.put("isIndividualName",ComDataUtil.getInstance().getIsIndividualByValue(isIndividual));
        m.put("groupName",social.getGroupName());
        m.put("theme",social.getTheme());
        m.put("digest",social.getTheme());
        m.put("selfAssessment",social.getSelfAssessment());
        m.put("process",social.getProcess());
        m.put("auditStatus", social.getAuditStatus());
        Integer status = social.getAuditStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));
        s = social.getStudent();
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
     * getSocialMapList 根据输入参数查询得到学生社会实践数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public  List getSocialMapList(String numName) {
        List dataList = new ArrayList();
        List<Social> sList = socialRepository.findSocialListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSocial(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询所有社会实践 按编号排序
     */
    public List getAllSocialMapList() {
        List dataList = new ArrayList();
        List<Social> sList = socialRepository.findAllSocialList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSocial(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询审核中状态社会实践 按编号排序
     */
    public List getWaitingSocialMapList() {
        List dataList = new ArrayList();
        List<Social> sList = socialRepository.findWaitingSocialList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSocial(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询已通过状态社会实践 按编号排序
     */
    public List getPassedSocialMapList() {
        List dataList = new ArrayList();
        List<Social> sList = socialRepository.findPassedSocialList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSocial(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询未通过状态社会实践 按编号排序
     */
    public List getFailedSocialMapList() {
        List dataList = new ArrayList();
        List<Social> sList = socialRepository.findUnpassedSocialList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSocial(sList.get(i)));
        }
        return dataList;
    }
    public List getSocialMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Social> sList = socialRepository.findSocialByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSocial(sList.get(i)));
        }
        return dataList;
    }

}

package org.fatmansoft.teach.service.creativePractice;


import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.Competition;
import org.fatmansoft.teach.repository.creativePratice.CompetitionRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompetitionService {
    @Autowired
    private CompetitionRepository competitionRepository;

    public Map getMapFromCompetition(Competition competition) {
        Map m = new HashMap();
        Student s;
        Teacher t;
        if (competition == null)
            return m;
        m.put("competitionId",competition.getCompetitionId());
        m.put("groupName",competition.getGroupName());
        m.put("member",competition.getMember());
        m.put("don",competition.getDon());
        m.put("awardStatus",competition.getAwardStatus());
        m.put("rank",competition.getRank());
        m.put("competitionTitle",competition.getTitle());
        m.put("competitionLevel",competition.getCompetitionLevel());
        m.put("auditStatus",competition.getAuditStatus());
        Integer status = competition.getAuditStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));
        s = competition.getStudent();
        t = competition.getTeacher();
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
     * getCompetitionMapList 根据输入参数查询得到学生社会实践数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getCompetitionMapList(String numName) {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findCompetitionListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        List<Competition> tList = competitionRepository.findCompetitionListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromCompetition(tList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询所有科研竞赛 按编号排序
     */
    public List getAllCompetitionMapList() {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findAllCompetitionList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询审核中状态科研竞赛 按编号排序
     */
    public List getWaitingCompetitionMapList() {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findWaitingCompetitionList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询已通过状态科研竞赛 按编号排序
     */
    public List getPassedCompetitionMapList() {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findPassedCompetitionList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询未通过状态社会实践 按编号排序
     */
    public List getFailedCompetitionMapList() {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findFailedCompetitionList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        return dataList;
    }
    public List getCompetitionMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findCompetitionByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        return dataList;
    }
    public List getCompetitionMapListByTeacherId(Integer teacherId) {
        List dataList = new ArrayList();
        List<Competition> sList = competitionRepository.findCompetitionByTeacherId(teacherId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCompetition(sList.get(i)));
        }
        return dataList;
    }
}

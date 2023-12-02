package org.fatmansoft.teach.service.studentInfo;

import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.Social;
import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.studentInfo.AchievementRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;


    /**
     * getMapFromAchievement 将学生成就属性数据转换复制MAp集合里
     */
    public Map getMapFromAchievement(Achievement achievement) {
        Map m = new HashMap();
        Student s;
        Teacher t;
        if (achievement == null)
            return m;
        m.put("achievementId",achievement.getAchievementId());
        m.put("achievementName", achievement.getName());
        m.put("achievementLevel", achievement.getLevel());
        m.put("time", achievement.getTime());
        m.put("type", achievement.getType());
        m.put("content", achievement.getContent());
        m.put("status", achievement.getStatus());
        Integer status = achievement.getStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));//性别类型的值转换成数据类型名
        s = achievement.getStudent();
        t = achievement.getTeacher();
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
     * getAchievementMapList 根据输入参数查询得到学生成就数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getAchievementMapList(String numName) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        List<Achievement> tList = achievementRepository.findAchievementListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromAchievement(tList.get(i)));
        }
        return dataList;
    }
    public List getPassedAchievementMapList(String numName) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findPassedAchievementListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        List<Achievement> tList = achievementRepository.findPassedAchievementListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromAchievement(tList.get(i)));
        }
        return dataList;
    }

    public List getWaitingAchievementMapList() {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findWaitingAchievementList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getPassedAchievementMapList() {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findPassedAchievementList(); //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getFailedAchievementMapList() {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findFailedAchievementList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }
    public List getAchievementMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getAchievementMapListByTeacherId(Integer teacherId) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementByTeacherId(teacherId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }
}

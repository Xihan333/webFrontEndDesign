package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.repository.student.ActivityRepository;
import org.fatmansoft.teach.models.student.Activity;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository sportActivityRepository;

    /**
     * getMapFromSportActivity 将体育活动属性数据转换复制MAp集合里
     */

    public Map getMapFromSportActivity(Activity activity) {
        Map m = new HashMap();
        Student s;
        if (activity == null)
            return m;
        m.put("day", activity.getDay());
        m.put("title", activity.getTitle());
        m.put("location", activity.getLocation());
        m.put("introduction", activity.getIntroduction());
        m.put("activityId", activity.getActivityId());
        s = activity.getStudent();
        if (s == null)
            return m;
        if(s != null){
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
        }
        return m;
    }

    /**
     * getSportActivityMapList 根据输入参数查询得体育活动数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */

    public List getSportActivityMapList(String dayTitle) {
        List dataList = new ArrayList();
        List<Activity> sList = sportActivityRepository.findSportActivityListByNumName(dayTitle);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromSportActivity(sList.get(i)));
        }
        return dataList;
    }

    public List getSportActivityMapListByStudentId(Integer studentId){
        List dataList = new ArrayList();
        List<Activity> sList = sportActivityRepository.findSportActivityByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++){
            dataList.add(getMapFromSportActivity(sList.get(i)));
        }
        return dataList;
    }

}

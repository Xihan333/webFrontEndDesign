package org.fatmansoft.teach.service.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.studentInfo.EducationExperience;
import org.fatmansoft.teach.repository.studentInfo.EducationExperienceRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EducationExperienceService {

    @Autowired
    private EducationExperienceRepository educationExperienceRepository;

    public Map getMapFromEducationExperience(EducationExperience educationExperience) {
        Map m = new HashMap();
        Student s;
        Teacher t;
        if (educationExperience == null)
            return m;
        m.put("educationExperienceId", educationExperience.getEducationExperienceId());
        m.put("educationExperienceName", educationExperience.getName());
        m.put("startTime", educationExperience.getStartTime());
        m.put("endTime", educationExperience.getEndTime());
        m.put("level", educationExperience.getLevel());
        m.put("Edescription", educationExperience.getDescription());
        m.put("teacher",educationExperience.getProofTeacher());
        m.put("position",educationExperience.getPosition());
        s = educationExperience.getStudent();
        t = educationExperience.getTeacher();
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


    public List getEducationExperienceMapList(String numName) {
        List dataList = new ArrayList();
        List<EducationExperience> sList = educationExperienceRepository.findEducationExperienceListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromEducationExperience(sList.get(i)));
        }
        return dataList;
    }

    public List getStudentEducationExperienceMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<EducationExperience> sList = educationExperienceRepository.findEducationExperienceListByStudentId(studentId);
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromEducationExperience(sList.get(i)));
        }
        return dataList;
    }
    public List getTeacherEducationExperienceMapList(Integer teacherId) {
        List dataList = new ArrayList();
        List<EducationExperience> sList = educationExperienceRepository.findEducationExperienceListByTeacherId(teacherId);
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromEducationExperience(sList.get(i)));
        }
        return dataList;
    }
}

package org.fatmansoft.teach.service.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.studentInfo.Family;
import org.fatmansoft.teach.repository.studentInfo.FamilyRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    public Map getMapFromFamily(Family family) {
        Map m = new HashMap();
        Student s;
        Teacher t;
        if (family == null)
            return m;
        m.put("familyId",family.getFamilyId());
        m.put("familyName", family.getName());
        m.put("familyGender", family.getGender());
        String familyGender = family.getGender();
        m.put("familyGender", family.getGender());
        m.put("familyGenderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", familyGender)); //性别类型的值转换成数据类型名
        m.put("familyBirthday", family.getBirthday());
        m.put("relation", family.getRelation());
        m.put("description", family.getDescription());
        s = family.getStudent();
        t = family.getTeacher();
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
    public List getFamilyMapList(String numName) {
        List dataList = new ArrayList();
        List<Family> sList = familyRepository.findFamilyListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromFamily(sList.get(i)));
        }
        return dataList;
    }


    public List getFamilyMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Family> sList = familyRepository.findFamilyListByStudentId(studentId);
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromFamily(sList.get(i)));
        }
        return dataList;
    }

    public List getFamilyMapListByTeacherId(Integer teacherId) {
        List dataList = new ArrayList();
        List<Family> sList = familyRepository.findFamilyListByTeacherId(teacherId);
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromFamily(sList.get(i)));
        }
        return dataList;
    }
}

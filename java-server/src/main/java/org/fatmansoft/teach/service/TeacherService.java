package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized Integer getNewTeacherId() {  //synchronized 同步方法
        Integer id = teacherRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    public Map getMapFromTeacher(Teacher s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        m.put("title",s.getTitle());
        m.put("degree",s.getDegree());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("teacherId", s.getTeacherId());
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    public List getTeacherMapList(String numName) {
        List dataList = new ArrayList();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromTeacher(sList.get(i)));
        }
        return dataList;
    }

}

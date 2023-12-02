package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.studentInfo.Family;
import org.fatmansoft.teach.models.study.Course;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.study.Score;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.FeeRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeeRepository feeRepository;

    /**
     * getMapFromStudent 将学生表属性数据转换复制MAp集合里
     */
    public Map getMapFromStudent(Student s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        m.put("major",s.getMajor());
        if(s.getClazz()!=null) {
            if(s.getClazz().getGrade()!=null){
                m.put("gradeName",s.getClazz().getGrade().getGradeName());
            }
            m.put("className", s.getClazz().getClazzName());
        }
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("studentId", s.getStudentId());
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

    /**
     *  getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return dataList;
    }

    /**
     * getStudentScoreList 将Score对象列表集合转换成Score Map对象列表集合
     * @param sList
     * @return
     */
    public List getStudentScoreList(List<Score>sList){
        List list = new ArrayList();
        if(sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for(Score s:sList){
            m = new HashMap();
            c = s.getCourse();
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("scoreId",s.getScoreId());
            m.put("courseNum", c.getNum());
            m.put("courseName", c.getName());
            m.put("credit", c.getCredit());
            m.put("mark", s.getMark());
            m.put("ranking", s.getRanking());
            list.add(m);
        }
        return list;
    }

    /**
     * getStudentMarkList 计算学生的的成绩等级
     * @param sList 学生成绩列表
     * @return 成绩等级Map对象列表
     */
    public List getStudentMarkList(List<Score> sList){
        String title[]={"优","良","中","及格","不及格"};
        int count[]= new int[5];
        List list = new ArrayList();
        if(sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for(Score s:sList){
            c = s.getCourse();
            if(s.getMark() >= 90)
                count[0]++;
            if(s.getMark() >= 80)
                count[1]++;
            if(s.getMark() >= 70)
                count[2]++;
            if(s.getMark() >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for(int i = 0; i < 5;i++) {
            m = new HashMap();
            m.put("title", title[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }

    /**
     * getStudentFeeList 获取学生的消费Map对象列表集合
     * @param studentId
     * @return
     */
    public List getStudentFeeList(Integer studentId){
        List<Fee> sList =feeRepository.findListByStudent(studentId);  // 查询某个学生消费记录集合
        List list = new ArrayList();
        if(sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for(Fee s:sList){
            m = new HashMap();
            m.put("title",s.getDay());
            m.put("value",s.getMoney());
            list.add(m);
        }
        return list;
    }
}

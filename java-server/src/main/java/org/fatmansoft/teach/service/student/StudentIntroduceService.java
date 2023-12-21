package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Achievement;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.teacher.ScientificPayoffsRepository;
import org.fatmansoft.teach.repository.student.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentIntroduceService {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private ScientificPayoffsRepository scientificPayoffsRepository;

    /**
     * 学生简历信息
     */
    public Map getIntroduceDataMap(Integer personId){
        Optional<Student> op = studentRepository.findByUserId(personId);
        Person p = op.get().getPerson();
        String name = "";
        if(op.isPresent()) {
            name = p.getName();
        }
        Map data = new HashMap();
        String html = "";
        if(html.length()> 0) {
            data.put("html", html);
            return data;
        }
        data.put("myName", name+"个人简历");
        List attachList = new ArrayList();
        Map o = new HashMap();
        o.put("title","基本信息");
        o.put("content",getStudentBasicInfo(op.get()));
        attachList.add(o);
        Map m = new HashMap();
        m.put("title","所获荣誉");
        m.put("content",getStudentAchievement(op.get().getStudentId()));
        attachList.add(m);
        data.put("attachList",attachList);
        return data;
    }

    public String getStudentBasicInfo(Student op){
        Map s = studentService.getMapFromStudent(op);
        String result = "";
        result += "<p><b>姓名</b>： "+s.get("name") + "</p>";
        result += "<p><b>性别</b>： "+s.get("genderName") + "</p>";
        result += "<p><b>学院</b>： "+s.get("dept") + "</p>";
        result += "<p><b>专业</b>： "+s.get("card") + "</p>";
        result += "<p><b>邮箱</b>： "+s.get("email") + "</p>";
        result += "<p><b>电话</b>： "+s.get("phone") + "</p>";
        result += "<p><b>住址</b>： "+s.get("address") + "</p>";
        return result;
    }

    //返回学生的荣誉信息
    public String getStudentAchievement(Integer studentId) {
        List<Achievement> pList = achievementRepository.findPassedAchievementByStudentId(studentId);
        if (pList == null || pList.size() == 0)
            return "<ul>无所获荣誉</ul>";
        String result = "", prizeLevel = "", prizeName = "", content = "";
        result += "<ul>";
        for (int i = 0; i < pList.size(); i++) {
            prizeLevel = pList.get(i).getLevel();
            prizeName = pList.get(i).getName();
            content = pList.get(i).getContent();
            result += "<p>" + (i + 1) + ". <b>级别</b>：" + prizeLevel + "</p>";
            result += "<p><b>荣誉名称</b>： " + prizeName + "</p>";
            result += "<p><b>荣誉详情</b>： " + content + "</p>";
        }
        result += "</ul>";
        return result;
    }
}

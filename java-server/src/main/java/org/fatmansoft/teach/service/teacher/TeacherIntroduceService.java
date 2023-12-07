package org.fatmansoft.teach.service.teacher;

import org.fatmansoft.teach.models.student.Achievement;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.teacher.ScientificPayoffs;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.repository.teacher.ScientificPayoffsRepository;
import org.fatmansoft.teach.repository.student.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherIntroduceService {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private ScientificPayoffsRepository scientificPayoffsRepository;


    public Map getIntroduceDataMap(Integer personId){
        Optional<Teacher> op = teacherRepository.findByPersonPersonId(personId);
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
        o.put("content",getTeacherBasicInfo(op.get()));
        attachList.add(o);
        Map m = new HashMap();
        m.put("title","所获荣誉");
        m.put("content",getTeacherAchievement(op.get().getTeacherId()));
        attachList.add(m);
        Map s = new HashMap();
        s.put("title","科研成果");
        s.put("content",getTeacherScientific(op.get().getTeacherId()));
        attachList.add(s);
        data.put("attachList",attachList);
        return data;
    }

    public String getTeacherBasicInfo(Teacher op){
        Map s = teacherService.getMapFromTeacher(op);
        String result = "";
        result += "<p><b>姓名</b>： "+s.get("name") + "</p>";
        result += "<p><b>性别</b>： "+s.get("genderName") + "</p>";
        result += "<p><b>学院</b>： "+s.get("dept") + "</p>";
        result += "<p><b>职称</b>： "+s.get("title") + "</p>";
        result += "<p><b>学位</b>： "+s.get("degree") + "</p>";
        result += "<p><b>邮箱</b>： "+s.get("email") + "</p>";
        result += "<p><b>电话</b>： "+s.get("phone") + "</p>";
        result += "<p><b>住址</b>： "+s.get("address") + "</p>";
        return result;
    }

    public String getTeacherAchievement(Integer teacherId){
        List<Achievement> pList = achievementRepository.findPassedAchievementByTeacherId(teacherId);
        if(pList==null||pList.size()==0)
            return "<ul>无所获荣誉</ul>" ;
        String result="", prizeLevel="", prizeName="", content="";
        result +="<ul>";
        for(int i=0;i<pList.size();i++){
            prizeLevel = pList.get(i).getLevel();
            prizeName = pList.get(i).getName();
            content = pList.get(i).getContent();
            result += "<p>" + (i+1) + ". <b>级别</b>：" + prizeLevel +"</p>";
            result += "<p><b>荣誉名称</b>： " + prizeName +"</p>";
            result += "<p><b>荣誉详情</b>： " + content +"</p>";
        }
        result +="</ul>";
        return result;
    }

    public String getTeacherScientific(Integer teacherId){
        List<ScientificPayoffs> pList = scientificPayoffsRepository.findScientificPayoffsByTeacherId(teacherId);
        if(pList==null||pList.size()==0)
            return "<ul>无科研成果</ul>" ;
        String result="", paperName="", day="", authors="";
        result +="<ul>";
        for(int i=0;i<pList.size();i++){
            paperName = pList.get(i).getPaperName();
            day = pList.get(i).getDay();
            authors = pList.get(i).getAuthors();
            result += "<p>" + (i+1) + ". <b>论文标题</b>：" + paperName  +"</p>";
            result += "<p><b>发表时间</b>： " + day +"</p>";
            result += "<p><b>作者</b>： " + authors +"</p>";
        }
        result +="</ul>";
        return result;
    }
}

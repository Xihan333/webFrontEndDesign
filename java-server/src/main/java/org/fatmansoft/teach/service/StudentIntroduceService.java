package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.creativePractice.Competition;
import org.fatmansoft.teach.models.creativePractice.NewProject;
import org.fatmansoft.teach.models.creativePractice.ScientificPayoffs;
import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.fatmansoft.teach.models.studentInfo.EducationExperience;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.creativePratice.CompetitionRepository;
import org.fatmansoft.teach.repository.creativePratice.NewProjectRepository;
import org.fatmansoft.teach.repository.creativePratice.ScientificPayoffsRepository;
import org.fatmansoft.teach.repository.studentInfo.AchievementRepository;
import org.fatmansoft.teach.repository.studentInfo.EducationExperienceRepository;
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
    private CompetitionRepository competitionRepository;

    @Autowired
    private NewProjectRepository newProjectRepository;

    @Autowired
    private ScientificPayoffsRepository scientificPayoffsRepository;
    @Autowired
    private EducationExperienceRepository educationExperienceRepository;

    /**
     * 学生简历信息
     */
    public Map getIntroduceDataMap(Integer personId){
        Optional<Student> op = studentRepository.findByPersonPersonId(personId);
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
        Map e = new HashMap();
        e.put("title","教育经历");
        e.put("content",getStudentEducation(op.get().getStudentId()));
        attachList.add(e);
        Map m = new HashMap();
        m.put("title","所获荣誉");
        m.put("content",getStudentAchievement(op.get().getStudentId()));
        attachList.add(m);
        Map c = new HashMap();
        c.put("title","竞赛经历");
        c.put("content",getStudentCompetition(op.get().getStudentId()));
        attachList.add(c);
        Map n = new HashMap();
        n.put("title","创新项目");
        n.put("content",getStudentProject(op.get().getStudentId()));
        attachList.add(n);
        Map s = new HashMap();
        s.put("title","科研成果");
        s.put("content",getStudentScientific(op.get().getStudentId()));
        attachList.add(s);
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

    public String getStudentEducation(Integer studentId){
        List<EducationExperience> pList = educationExperienceRepository.findEducationExperienceListByStudentId(studentId);
        if(pList==null||pList.size()==0)
            return "<ul>无教育经历</ul>" ;
        String result="", schoolName="", level="", startTime="",endTime="";
        result +="<ul>";
        for(int i=0;i<pList.size();i++){
            schoolName = pList.get(i).getName();
            level = pList.get(i).getLevel();
            startTime = pList.get(i).getStartTime();
            if(pList.get(i).getEndTime().length()==0){endTime = "今";}
            else{endTime = pList.get(i).getEndTime();}
            result += "<p>" + (i+1) + ". <b>学校名称</b>：" + schoolName +"</p>";
            result += "<p><b>时间</b>： 自" + startTime + "\t" + "至 "+ endTime + "</p>";
            result += "<p><b>级别</b>： " + level +"</p>";
        }
        result +="</ul>";
        return result;
    }
    //返回学生的荣誉信息
    public String getStudentAchievement(Integer studentId){
        List<Achievement> pList = achievementRepository.findPassedAchievementByStudentId(studentId);
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
    public String getStudentCompetition(Integer studentId){
        List<Competition> pList = competitionRepository.findCompetitionByStudentId(studentId);
        if(pList==null||pList.size()==0)
            return "<ul>无竞赛获奖</ul>" ;
        String result="", competitionLevel="", competitionName="", awardStatus="";
        result +="<ul>";
        for(int i=0;i<pList.size();i++){
            competitionLevel = pList.get(i).getCompetitionLevel();
            competitionName = pList.get(i).getTitle();
            awardStatus = pList.get(i).getAwardStatus();
            result += "<p>" + (i+1) + ". <b>级别</b>：" + competitionLevel  +"</p>";
            result += "<p><b>竞赛名称</b>： " + competitionName +"</p>";
            result += "<p><b>获奖情况</b>： " + awardStatus +"</p>";
        }
        result +="</ul>";
        return result;
    }
    public String getStudentProject(Integer studentId){
        List<NewProject> pList = newProjectRepository.findNewProjectByStudentId(studentId);
        if(pList==null||pList.size()==0)
            return "<ul>无创新项目</ul>" ;
        String result="", projectName="", groupName="", exposition="";
        result +="<ul>";
        for(int i=0;i<pList.size();i++){
            projectName = pList.get(i).getProjectName();
            groupName = pList.get(i).getGroupName();
            exposition = pList.get(i).getExposition();
            result += "<p>" + (i+1) + ". <b>项目名称</b>：" + projectName  +"</p>";
            result += "<p><b>团队名称</b>： " + groupName +"</p>";
            result += "<p><b>阐述及成果展示</b>： " + exposition +"</p>";
        }
        result +="</ul>";
        return result;
    }
    public String getStudentScientific(Integer studentId){
        List<ScientificPayoffs> pList = scientificPayoffsRepository.findScientificPayoffsByStudentId(studentId);
        if(pList==null||pList.size()==0)
            return "<ul>无科研成果</ul>" ;
        String result="", paperName="", identity="", periodical="";
        result +="<ul>";
        for(int i=0;i<pList.size();i++){
            paperName = pList.get(i).getPaperName();
            identity = pList.get(i).getIdentity();
            periodical = pList.get(i).getPeriodical();
            result += "<p>" + (i+1) + ". <b>论文标题</b>：" + paperName  +"</p>";
            result += "<p><b>担任角色</b>： " + identity +"</p>";
            result += "<p><b>发表期刊</b>： " + periodical +"</p>";
        }
        result +="</ul>";
        return result;
    }
}

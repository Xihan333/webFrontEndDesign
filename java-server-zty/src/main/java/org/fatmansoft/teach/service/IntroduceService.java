package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntroduceService {
    @Autowired
    private StudentRepository studentRepository;
    public String getHtmlCount(String name){
        String content = "";
        content= "<!DOCTYPE html>";
        content += "<html>";
        content += "<head>";
        content += "<style>";
        content += "html { font-family: \"SourceHanSansSC\", \"Open Sans\";}";
        content += "</style>";
        content += "<meta charset='UTF-8' />";
        content += "<title>Insert title here</title>";
        content += "</head>";
        content += "<body>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        "+name+ " HTML </tr>";
        content += "   </thead>";
        content += "   </table>";


        content += "</body>";
        content += "</html>";
        return content;
    }
    //个人简历信息数据准备方法  请同学修改这个方法，请根据自己的数据的希望展示的内容拼接成字符串，放在Map对象里， attachList 可以方多段内容，具体内容有个人决定
    public Map getIntroduceDataMap(Integer studentId){
        Optional<Student> op = studentRepository.findById(studentId);
        String name = "";
        if(op.isPresent()) {
            name = op.get().getStudentName();
        }
        Map data = new HashMap();
        String html = "";
//        html = getHtmlCount(name);
        if(html.length()> 0) {
            data.put("html", html);
            return data;
        }
        data.put("myName", name);   // 学生信息
        data.put("overview","本人.");  //学生基本信息综述
        List attachList = new ArrayList();
        Map m;
        m = new HashMap();
        m.put("title","学习成绩");   //
        m.put("content","成绩...");  // 学生成绩综述
        attachList.add(m);
        m = new HashMap();
        m.put("title","社会实践");
        m.put("content","社会实践...");  // 社会实践综述
        attachList.add(m);
        data.put("attachList",attachList);
        return data;
    }
}

package org.fatmansoft.teach.service.study;

import org.fatmansoft.teach.models.study.Attendance;
import org.fatmansoft.teach.models.study.Homework;
import org.fatmansoft.teach.repository.study.HomeworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeworkService {
    @Autowired
    private HomeworkRepository homeworkRepository;

    public synchronized Integer getNewHomeworkId() {  //synchronized 同步方法
        Integer id = homeworkRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    public Map getMapFromHomework(Homework h) {
        Map m=new HashMap<>();
        m.put("homeworkId", h.getId());
        m.put("studentId", h.getScore().getStudent().getStudentId());
        m.put("courseId", h.getScore().getCourse().getCourseId());
        m.put("studentNum", h.getScore().getStudent().getPerson().getNum());
        m.put("studentName", h.getScore().getStudent().getPerson().getName());
        m.put("clazzName", h.getScore().getStudent().getClazz().getClazzName());
        m.put("gradeName", h.getScore().getStudent().getClazz().getGrade().getGradeName());
        m.put("courseNum", h.getScore().getCourse().getNum());
        m.put("courseName", h.getScore().getCourse().getName());
        m.put("credit", h.getScore().getCourse().getCredit());
        m.put("homeworkName", h.getName());
        m.put("mark", h.getMark());
        return m;
    }

    public List getHomeworkMapList(List<Homework> list){
        List dataList = new ArrayList();
        if(list == null || list.size() == 0)
            return dataList;
        for(int i = 0; i < list.size();i++) {
            dataList.add(getMapFromHomework(list.get(i)));
        }
        return dataList;
    }
}

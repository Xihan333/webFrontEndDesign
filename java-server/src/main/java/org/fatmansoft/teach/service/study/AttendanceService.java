package org.fatmansoft.teach.service.study;

import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.TeacherCourse;
import org.fatmansoft.teach.models.study.Attendance;
import org.fatmansoft.teach.models.study.Course;
import org.fatmansoft.teach.repository.study.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AttendanceService {

   @Autowired
   private AttendanceRepository attendanceRepository;

    public synchronized Integer getNewAttendanceId() {  //synchronized 同步方法
        Integer id = attendanceRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    public Map getMapFromAttendance(Attendance a) {
        Map m=new HashMap<>();
        m.put("attendanceId", a.getId());
        m.put("studentId", a.getScore().getStudent().getStudentId());
        m.put("courseId", a.getScore().getCourse().getCourseId());
        m.put("studentNum", a.getScore().getStudent().getPerson().getNum());
        m.put("studentName", a.getScore().getStudent().getPerson().getName());
        m.put("clazzName", a.getScore().getStudent().getClazz().getClazzName());
        m.put("gradeName", a.getScore().getStudent().getClazz().getGrade().getGradeName());
        m.put("courseNum", a.getScore().getCourse().getNum());
        m.put("courseName", a.getScore().getCourse().getName());
        m.put("credit", a.getScore().getCourse().getCredit());
        m.put("date", a.getDate());
        m.put("situation", a.getSituation());
        return m;
    }

    public List getAttendanceMapList(List<Attendance> list){
        List dataList = new ArrayList();
        if(list == null || list.size() == 0)
            return dataList;
        for(int i = 0; i < list.size();i++) {
            dataList.add(getMapFromAttendance(list.get(i)));
        }
        return dataList;
    }
}

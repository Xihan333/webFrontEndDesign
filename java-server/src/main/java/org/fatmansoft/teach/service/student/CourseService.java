package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Course;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.CourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    public synchronized Integer getNewCourseId() {  //synchronized 同步方法
        Integer id = courseRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    /**
     * getMapFromCourse 将课程表属性数据转换复制MAp集合里
     */
    public Map getMapFromCourse(Course c) {
        Map m = new HashMap();
        if(c == null) {
            return m;
        }
        m.put("courseId", c.getCourseId());
        m.put("courseName",c.getName());
        m.put("courseNum",c.getNum());
        if(c.getGrade()!=null){
            m.put("gradeId",c.getGrade().getGradeId());
            m.put("gradeName",c.getGrade().getGradeName());
        }
        m.put("hour",c.getHour());
        m.put("credit",c.getCredit());
        m.put("day",c.getDay());
        m.put("timeOrder",c.getTimeOrder());
        m.put("place",c.getPlace());
        List<Teacher> tList = teacherRepository.findTeacherListByCourseCourseId(c.getCourseId());
        if(!tList.isEmpty()){
            ArrayList<Integer> teacherIdList=new ArrayList<>();
            ArrayList<String> teacherNameList=new ArrayList<>();
            ArrayList<String> teacherNumList=new ArrayList<>();
            for(int i=0;i<tList.size();i++){
                teacherIdList.add(tList.get(i).getTeacherId());
                teacherNameList.add(tList.get(i).getPerson().getName());
                teacherNumList.add(tList.get(i).getPerson().getNum());
            }
            m.put("teacherIdList",teacherIdList);
            m.put("teacherNameList",teacherNameList);
            m.put("teacherNumList",teacherNumList);
        }
        return m;
    }

    public List getCourseMapList(List<Course> list){
        List dataList = new ArrayList();
        if(list == null || list.size() == 0)
            return dataList;
        for(int i = 0; i < list.size();i++) {
            dataList.add(getMapFromCourse(list.get(i)));
        }
        return dataList;
    }

    public DataResponse getCourseOptionItemListByGradeId(DataRequest dataRequest) {
        Integer gradeId=dataRequest.getInteger("gradeId");
        List<Course> cList = courseRepository.findCourseListByGradeGradeId(gradeId);  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Course c : cList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getNum(), c.getNum()+"-"+c.getName()));
        }
        return CommonMethod.getReturnData(itemList);

    }
}

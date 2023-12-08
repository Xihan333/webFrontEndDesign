package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.*;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.Const;
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

    @Autowired
    private CourseService courseService;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private CampusRepository campusRepository;


    public synchronized Integer getNewCourseId() {  //synchronized 同步方法
        Integer id = courseRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    public DataResponse getCoursesByGradeId(DataRequest dataRequest) {
        Integer gradeId=dataRequest.getInteger("gradeId");
        List<TeacherCourse> cList = teacherCourseRepository.findTeacherCourseListByGradeGradeId(gradeId);  //数据库查询操作
        List dataList = new ArrayList();
        if(cList == null || cList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0; i < cList.size(); i++) {
            dataList.add(teacherCourseService.getMapFromTeacherCourse(cList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse deleteCourse(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");  //获取course_id值
        Course course= null;
        Optional<Course> opCourse;
        if(courseId != null) {
            opCourse= courseRepository.findById(courseId);   //查询获得实体对象
            if(opCourse.isPresent()) {
                course = opCourse.get();
            }
        }
        if(course != null) {
            List<Score> list = scoreRepository.findScoreListByCourseId(courseId);
            scoreRepository.deleteAll(list);
            courseRepository.deleteById(courseId);
            List<TeacherCourse> sList = teacherCourseRepository.findTeacherCourseListByCourseId(courseId);  //数据库查询操作
            teacherCourseRepository.deleteAll(sList);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

}

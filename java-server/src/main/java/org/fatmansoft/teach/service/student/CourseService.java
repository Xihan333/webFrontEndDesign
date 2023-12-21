package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.*;
import org.fatmansoft.teach.models.system.StaticValue;
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

    public Map getMapFromCourse(Course tc) {
        Map m = new HashMap();
        if (tc == null) {
            return m;
        }
        m.put("courseId",tc.getCourseId());
        m.put("courseName", tc.getName());
        m.put("courseNum", tc.getNum());
        if (tc.getCampus() != null) {
            m.put("campusId", tc.getCampus().getCampusId());
            m.put("campusName", tc.getCampus().getName());
        }
        if (tc.getGrade() != null) {
            m.put("gradeId", tc.getGrade().getGradeId());
            m.put("gradeName", tc.getGrade().getGradeName());
        }
        m.put("hour", tc.getHour());
        m.put("type", tc.getType());
        m.put("credit", tc.getCredit());
        m.put("introduction", tc.getIntroduction());
        return m;
    }

    public List CourseList(List<Course> sList) {
        List dataList = new ArrayList();
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCourse(sList.get(i)));
        }
        return dataList;
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
        Course course = null;
        Optional<Course> opCourse;
        if(courseId != null) {
            opCourse= courseRepository.findById(courseId);   //查询获得实体对象
            if(opCourse.isPresent()) {
                course = opCourse.get();
            }else{
                return CommonMethod.getReturnMessageError("不存在该学科！");
            }
        }
        Optional<List<TeacherCourse>> optionalTeacherCourses = Optional.ofNullable(teacherCourseRepository.findTeacherCourseListByCourseId(courseId));
        if (optionalTeacherCourses.isPresent() && !optionalTeacherCourses.get().isEmpty()) {
            List<TeacherCourse> teacherCourses = optionalTeacherCourses.get();
            teacherCourseRepository.deleteAll(teacherCourses);
        }

        Optional<List<Score>> optionalScores = Optional.ofNullable(scoreRepository.findScoreListByCourseId(courseId));
        if(optionalScores.isPresent() && !optionalScores.get().isEmpty()){
            List<Score> list = optionalScores.get();
            scoreRepository.deleteAll(list);
        }

        courseRepository.deleteById(courseId);

        courseRepository.delete(course);
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse addCourse(DataRequest dataRequest) {
        Integer courseId = getNewCourseId();
        Map map = dataRequest.getMap("form");
        String courseNum = CommonMethod.getString(map,"courseNum"); //课序号
        if(courseRepository.findByNum(courseNum).isPresent()){
            return CommonMethod.getReturnMessageError("课序号已被占用！");
        }
        Course course = new Course();
        course.setCourseId(courseId);
        course.setNum(courseNum);

        Grade grade;
        Optional<Grade> opGrade;
        Integer gradeId = CommonMethod.getInteger(map,"gradeId");
        opGrade = gradeRepository.findByGradeId(gradeId);
        if(opGrade.isEmpty()){
            return CommonMethod.getReturnMessageError("年级不存在！");
        }else{
            grade = opGrade.get();
        }
        course.setGrade(grade);

        Campus campus;
        Optional<Campus> opCampus;
        Integer campusId = CommonMethod.getInteger(map,"campusId");
        opCampus = campusRepository.findByCampusId(campusId);
        if(opCampus.isPresent()){
            campus = opCampus.get();
        }else{
            return CommonMethod.getReturnMessageError("学院不存在！");
        }
        course.setCampus(campus);

        course.setName(CommonMethod.getString(map,"courseName"));
        course.setHour(CommonMethod.getInteger(map,"hour"));
        course.setCredit(CommonMethod.getInteger(map,"credit"));
        course.setType(CommonMethod.getInteger(map,"courseType"));
        course.setIntroduction(CommonMethod.getString(map,"introduction"));
        courseRepository.save(course);

        return CommonMethod.getReturnData(courseService.getMapFromCourse(course));
    }


    public DataResponse editCourse(DataRequest dataRequest) {
        Map map = dataRequest.getMap("form");
        Integer courseId = CommonMethod.getInteger(map,"courseId");
        String courseNum = CommonMethod.getString(map,"courseNum"); //课序号
        Optional<Course> courseOptional = courseRepository.findByCourseId(courseId);
        System.out.println(courseOptional);
        Course course = null;
        if(courseOptional.isPresent()){
            course = courseOptional.get();
        }else{
            return CommonMethod.getReturnMessageError("该课程不存在！");
        }
        course.setNum(courseNum);
        Grade grade;
        Optional<Grade> opGrade;
        Integer gradeId = CommonMethod.getInteger(map,"gradeId");
        opGrade = gradeRepository.findByGradeId(gradeId);
        if(opGrade.isEmpty()){
            return CommonMethod.getReturnMessageError("年级不存在！");
        }else{
            grade = opGrade.get();
        }
        course.setGrade(grade);

        Campus campus;
        Optional<Campus> opCampus;
        Integer campusId = CommonMethod.getInteger(map,"campusId");
        opCampus = campusRepository.findByCampusId(campusId);
        if(opCampus.isPresent()){
            campus = opCampus.get();
        }else{
            return CommonMethod.getReturnMessageError("学院不存在！");
        }
        course.setCampus(campus);

        course.setName(CommonMethod.getString(map,"courseName"));
        course.setHour(CommonMethod.getInteger(map,"hour"));
        course.setCredit(CommonMethod.getInteger(map,"credit"));
        course.setType(CommonMethod.getInteger(map,"courseType"));
        course.setIntroduction(CommonMethod.getString(map,"introduction"));
        courseRepository.save(course);

        return CommonMethod.getReturnData(courseService.getMapFromCourse(course));
    }

    public DataResponse getAllCourses() {
        List<Course> sList = courseRepository.findAll(); //数据库查询操作
        List dataList = CourseList(sList);
        return CommonMethod.getReturnData(dataList);
    }
}

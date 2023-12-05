package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Campus;
import org.fatmansoft.teach.models.student.Grade;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Course;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.student.GradeService;
import org.fatmansoft.teach.service.student.CourseService;
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

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

    @PostMapping("/getCourseOptionItemListByGradeId")
    public DataResponse getCourseOptionItemListByGradeId(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseOptionItemListByGradeId(dataRequest);
    }

    @PostMapping("/getCourseOptionItemListByStudentId")
    public DataResponse getCourseOptionItemListByStudentId(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseOptionItemListByStudentId(dataRequest);
    }

    @PostMapping("/getCourseOptionItemListByTeacherId")
    public OptionItemList getCourseOptionItemListByTeacherId(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId=0;
        Integer userId=dataRequest.getInteger("userId");
        if(userId!=null){
            Teacher teacher=null;
            Optional<Teacher> opT=teacherRepository.findByUserId(userId);
            if(opT.isPresent()){
                teacher=opT.get();
            }
            if(teacher!=null){
                teacherId=teacher.getTeacherId();
            }
        }
        else {
            teacherId=dataRequest.getInteger("teacherId");
        }
        List<Course> cList = courseRepository.findCourseListByTeacherId(teacherId);  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Course c : cList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getNum(), c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getByStudentIdAndNumName")
    /*学生根据课程名/课序号查询课程
      所需参数 userId numName
     */
    public DataResponse getByStudentIdAndNumName(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId=dataRequest.getInteger("userId");
        String numName= dataRequest.getString("numName");
        Integer studentId=0;
        Student student=null;
        Optional<Student> opS=studentRepository.findByUserId(userId);
        if(opS.isPresent()){
            student=opS.get();
        }
        if(student!=null){
            studentId=student.getStudentId();
        }
        List<Course> cList = courseRepository.findCourseListByStudentIdAndNumName(studentId,numName);
        List dataList = courseService.getCourseMapList(cList);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getByTeacherIdAndNumName")
    /*教师根据课程名/课序号查询课程
      所需参数 userId numName
     */
    public DataResponse getByTeacherIdAndNumName(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId=dataRequest.getInteger("userId");
        String numName= dataRequest.getString("numName");
        Integer teacherId=0;
        Teacher teacher=null;
        Optional<Teacher> opT=teacherRepository.findByUserId(userId);
        if(opT.isPresent()){
            teacher=opT.get();
        }
        if(teacher!=null){
            teacherId=teacher.getTeacherId();
        }
        List<Course> cList = courseRepository.findCourseListByTeacherIdAndNumName(teacherId,numName);  //数据库查询操作
        List dataList = courseService.getCourseMapList(cList);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/getCoursesByCourseNumName")
    /*管理员根据课程名/课序号查询课程
      所需参数 numName
     */
    public DataResponse getCoursesByCourseNumName(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List dataList = courseService.getCourseMapList(cList);
        return CommonMethod.getReturnData(dataList);  //根据框架规范，应返回Map 的list
    }

    @PostMapping("/getByCourseId")
    /*根据课程id查询课程
      所需参数 courseId
    */
    public DataResponse getByCourseId(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId= dataRequest.getInteger("courseId");
        Optional<Course> opCourse = courseRepository.findByCourseId(courseId);
        Course course=opCourse.get();
        Map data=courseService.getMapFromCourse(course);
        return CommonMethod.getReturnData(data);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getByStudentId")
    /*根据学生查询课程 studentId
      所需参数 studentId
    */
    //此处可能有bug
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId= dataRequest.getInteger("studentId");
        List<Course> dataList = courseRepository.findCourseListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/addOrEditCourse")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员修改添加课程
      所需参数 courseId teacherCourseId form(courseNum,courseName,gradeName,hour,credit,time,place,teacherNum,teacherName)
    */
    public DataResponse addOrEditCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.addOrEditCourse(dataRequest);
    }

    @PostMapping("/deleteCourse")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员删除课程
      所需参数 courseId
     */
    public DataResponse deleteCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.deleteCourse(dataRequest);
    }

    /**
     * 根据：必修 限选 任选 0 1 2 type 获取所有课程
     * 选课时可以根据type 必修 任选等分类
     * @param dataRequest
     * @return
     */
    @PostMapping("getCoursesByType")
    public DataResponse getCoursesByType(@Valid @RequestBody DataRequest dataRequest){
        return courseService.getCoursesByType(dataRequest);
    }

    //根据教师名字查询课程
    @PostMapping("/getCoursesByTeacherName")
    public DataResponse getCoursesByTeacherName(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCoursesByTeacherName(dataRequest);
    }


    //根据课程开设时间查询课程  day timeOrder
    @PostMapping("/getCoursesByDayTimeOrder")
    public DataResponse getCoursesByDayTimeOrder(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCoursesByDayTimeOrder(dataRequest);
    }

    @PostMapping("/selectCourse")
    public DataResponse selectCourse(@Valid @RequestBody DataRequest dataRequest){
        return courseService.selectCourse(dataRequest);
    }

    //TODO：管理增删某一门课程的学生
}

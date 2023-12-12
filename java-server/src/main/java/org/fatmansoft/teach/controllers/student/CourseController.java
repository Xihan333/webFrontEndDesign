package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Course;
import org.fatmansoft.teach.models.system.StaticValue;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.StaticValueRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.student.GradeService;
import org.fatmansoft.teach.service.student.CourseService;
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.crypto.Data;
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

    @Autowired
    private StaticValueRepository staticValueRepository;

    //根据年级GradeId获取课程列表
    @PostMapping("/getCoursesByGradeId")
    public DataResponse getCoursesByGradeId(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCoursesByGradeId(dataRequest);
    }

    //获取我的课表（学生端）
    @GetMapping("/getMyCourses")
    public DataResponse getMyCourses() {
        return teacherCourseService.getMyCourses();
    }

    //获取我教的课程（教师端）
    @GetMapping("/getMyTeacherCourses")
    public DataResponse getMyTeacherCourses() {
        return teacherCourseService.getMyTeacherCourses();
    }

    //根据教师teacherId获取教授的课程
    @PostMapping("/getCoursesByTeacherId")
    public DataResponse getCoursesByTeacherId(@Valid @RequestBody DataRequest dataRequest) {
        return teacherCourseService.getCoursesByTeacherId(dataRequest);
    }


    //根据课程名/课序号查询课程
    @PostMapping("/getByCourseNumName")
    public DataResponse getByCourseNumName(@Valid @RequestBody DataRequest dataRequest) {
        return teacherCourseService.getByCourseNumName(dataRequest);
    }


    //添加课程
    @PostMapping("/addCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addCourse(@Valid @RequestBody DataRequest dataRequest) {
        return  teacherCourseService.addCourse(dataRequest);
    }

    //编辑课程
    @PostMapping("/editCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse editCourse(@Valid @RequestBody DataRequest dataRequest) {
        return  teacherCourseService.editCourse(dataRequest);
    }

    //删除课程 courseId
    @PostMapping("/deleteCourse")
    @PreAuthorize("hasRole('ADMIN')")
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
        return teacherCourseService.getCoursesByType(dataRequest);
    }

    //根据教师名字查询课程
    @PostMapping("/getCoursesByTeacherName")
    public DataResponse getCoursesByTeacherName(@Valid @RequestBody DataRequest dataRequest) {
        return teacherCourseService.getCoursesByTeacherName(dataRequest);
    }


    //根据课程开设时间查询课程  day timeOrder
    @PostMapping("/getCoursesByDayTimeOrder")
    public DataResponse getCoursesByDayTimeOrder(@Valid @RequestBody DataRequest dataRequest) {
        return teacherCourseService.getCoursesByDayTimeOrder(dataRequest);
    }

    @PostMapping("/selectCourse")
    public DataResponse selectCourse(@Valid @RequestBody DataRequest dataRequest){
        return teacherCourseService.selectCourse(dataRequest);
    }


    //退课
    @PostMapping("/cancelCourse")
    public DataResponse cancelCourse(@Valid @RequestBody DataRequest dataRequest){
        return teacherCourseService.cancelCourse(dataRequest);
    }

    /**
     * 开启选课（管理员）
     * @param dataRequest    string selectAvailable  1表示选课关闭 0表示开启
     * @return
     */
    @PostMapping("/changeCourseSelectAvailable")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse changeCourseSelectAvailable(@Valid @RequestBody DataRequest dataRequest){
        String select_available = dataRequest.getString("selectAvailable");
        StaticValue available = staticValueRepository.findById(Const.COURSE_SELECT_AVAILABLE).orElse(null);
        if(select_available.equals("1") || select_available.equals("0")){
            available.setValue(select_available);
        }else{
            return CommonMethod.getReturnMessageError("获取参数值未知！");
        }
        staticValueRepository.save(available);
        return CommonMethod.getReturnData(available.getValue());
    }
}

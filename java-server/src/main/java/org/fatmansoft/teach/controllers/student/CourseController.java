package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Grade;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Course;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.CourseRepository;
import org.fatmansoft.teach.repository.student.GradeRepository;
import org.fatmansoft.teach.repository.student.ScoreRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
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

    @PostMapping("/getCourseOptionItemListByGradeId")
    public DataResponse getCourseOptionItemListByGradeId(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseOptionItemListByGradeId(dataRequest);
    }
    @PostMapping("/getCourseOptionItemListByStudentId")
    public OptionItemList getCourseOptionItemListByStudentId(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId=0;
        Integer userId=dataRequest.getInteger("userId");
        if(userId!=null){
            Student student=null;
            Optional<Student> opS=studentRepository.findByUserId(userId);
            if(opS.isPresent()){
                student=opS.get();
            }
            if(student!=null){
                studentId=student.getStudentId();
            }
        }
        else {
            studentId=dataRequest.getInteger("studentId");
        }
        List<Course> cList = courseRepository.findCourseListByStudentId(studentId);  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Course c : cList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getNum(), c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
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
    @PostMapping("/getByNumName")
    /*管理员根据课程名/课序号查询课程
      所需参数 numName
     */
    public DataResponse getByNumName(@Valid @RequestBody DataRequest dataRequest) {
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

    @PostMapping("/addOrEdit")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员修改添加课程
      所需参数 courseId teacherCourseId form(courseNum,courseName,gradeName,hour,credit,time,place,teacherNum,teacherName)
    */
    public DataResponse addOrEdit(@Valid @RequestBody DataRequest dataRequest) {
        Map map=dataRequest.getMap("form");

        //校验数据
        Integer courseId=dataRequest.getInteger("courseId");
        String courseNum=CommonMethod.getString(map,"courseNum");
        if(courseId==null){
            Optional<Course> opC=courseRepository.findByNum(courseNum);
            if(opC.isPresent()){
                return CommonMethod.getReturnMessageError("课序号已存在，请检查是否正确！");
            }
        }
        else {
            Optional<Course> opC=courseRepository.findByCourseId(courseId);
            if(!opC.get().getNum().equals(courseNum)){
                opC=courseRepository.findByNum(courseNum);
                if(opC.isPresent()){
                    return CommonMethod.getReturnMessageError("课序号已存在，请检查是否正确！");
                }
            }
        }

        String gradeName=CommonMethod.getString(map,"gradeName");
        if(gradeName!="") {
            Optional<Grade> opGrade = gradeRepository.findByGradeName(gradeName);
            if (!opGrade.isPresent()) {
                return CommonMethod.getReturnMessageError("该年级不存在，请检查格式是否正确！");
            }
        }

        ArrayList<String> teacherNumList=(ArrayList<String>) CommonMethod.getList(map,"teacherNumList");
        ArrayList<String> teacherNameList=(ArrayList<String>) CommonMethod.getList(map,"teacherNameList");
        for(int i=0;i<teacherNumList.size();i++){
            if(teacherNumList.get(i)!=null) {
                Optional<Teacher> opTeacher = teacherRepository.findByPersonNum(teacherNumList.get(i));
                if (!opTeacher.isPresent()||!teacherNameList.get(i).equals("")&&!(opTeacher.isPresent()&&opTeacher.get().getPerson().getName().equals(teacherNameList.get(i)))) {
                    return CommonMethod.getReturnMessageError("该教师不存在，请检查姓名或工号是否正确！");
                }
            }
        }

        Course course;
        if(courseId!=null){
            Optional<Course> opCourse=courseRepository.findById(courseId);
            if(opCourse.isPresent()) {
                course = opCourse.get();
            }
            else {
                course=new Course();
                course.setCourseId(courseId);
            }
        }
        else {
            course=new Course();
            courseId=courseService.getNewCourseId();
            course.setCourseId(courseId);
            course.setNum(CommonMethod.getString(map,"courseNum"));
            courseRepository.saveAndFlush(course);
        }

        //需要修改年级
        Grade grade;
        Optional<Grade> opGrade;
        Integer gradeId=dataRequest.getInteger("gradeId");
        if(gradeName!=""){
            if(gradeId!=null&&gradeRepository.findById(gradeId).isPresent()) {
                grade = gradeRepository.findById(gradeId).get();
                if (!gradeName.equals(grade.getGradeName())) {
                    opGrade = gradeRepository.findByGradeName(gradeName);
                    grade = opGrade.get();
                }
            }
            else {
                opGrade=gradeRepository.findByGradeName(gradeName);
                grade = opGrade.get();
            }
            course.setGrade(grade);
        }
        else {
            course.setGrade(null);
        }

        //需要修改老师
        ArrayList<Integer> teacherIdList=(ArrayList<Integer>) dataRequest.getList("teacherIdList");
        if(teacherNumList.size()!=0){
            for(int i=0;i<teacherNumList.size();i++) {
                //编辑后的教师数量多于原来的
                if(i==teacherIdList.size()){
                    break;
                }
                Teacher teacher;
                Optional<Teacher> opTeacher;
                if(i<teacherIdList.size()&&teacherRepository.findById(teacherIdList.get(i)).isPresent()) {
                    teacher = teacherRepository.findById(teacherIdList.get(i)).get();
                    if (!teacherNumList.get(i).equals(teacher.getPerson().getNum())) {
                        //删除原记录
                        teacherCourseRepository.deleteByCourseCourseIdAndTeacherTeacherId(courseId,teacherIdList.get(i));
                        opTeacher=teacherRepository.findByPersonNum(teacherNumList.get(i));
                        teacher=opTeacher.get();
                    }
                    else {
                        continue;
                    }
                }
                else {
                    opTeacher=teacherRepository.findByPersonNum(teacherNumList.get(i));
                    teacher=opTeacher.get();
                }
                //创建新记录
                TeacherCourse teacherCourse=new TeacherCourse();
                teacherCourse.setId(teacherCourseService.getNewTeacherCourseId());
                teacherCourse.setCourse(course);
                teacherCourse.setTeacher(teacher);
                teacherCourseRepository.saveAndFlush(teacherCourse);
            }
            //新增老师
            for(int i=teacherIdList.size();i<teacherNumList.size();i++){
                Optional<Teacher> opTeacher=teacherRepository.findByPersonNum(teacherNumList.get(i));
                Teacher teacher=opTeacher.get();
                //创建新记录
                TeacherCourse teacherCourse=new TeacherCourse();
                teacherCourse.setId(teacherCourseService.getNewTeacherCourseId());
                teacherCourse.setCourse(course);
                teacherCourse.setTeacher(teacher);
                teacherCourseRepository.saveAndFlush(teacherCourse);
            }
            //减少老师
            for(int i=teacherNumList.size();i<teacherIdList.size();i++){
                teacherCourseRepository.deleteByCourseCourseIdAndTeacherTeacherId(courseId,teacherIdList.get(i));
            }
        }
        else {
            teacherCourseRepository.deleteByCourseCourseId(courseId);
        }

        course.setNum(CommonMethod.getString(map,"courseNum"));
        course.setName(CommonMethod.getString(map,"courseName"));
        course.setHour(CommonMethod.getInteger(map,"hour"));
        course.setCredit(CommonMethod.getInteger(map,"credit"));
        course.setDay(CommonMethod.getInteger(map,"day"));
        course.setTimeOrder(CommonMethod.getInteger(map,"timeOrder"));
        course.setPlace(CommonMethod.getString(map,"place"));
        courseRepository.save(course);

        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员删除课程
      所需参数 courseId
     */
    public DataResponse delete(@Valid @RequestBody DataRequest dataRequest) {
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
            //先删除从表
            scoreRepository.deleteByCourseCourseId(courseId);
            teacherCourseRepository.deleteByCourseCourseId(courseId);
            //再删除主表
            courseRepository.deleteById(courseId);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //TODO：选课时可以根据type 必修 任选等分类

    //TODO：根据教师 课序号 时间

    //TODO：管理增删某一门课程的学生
}

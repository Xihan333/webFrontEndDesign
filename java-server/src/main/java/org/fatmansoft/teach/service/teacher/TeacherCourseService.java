package org.fatmansoft.teach.service.teacher;

import org.fatmansoft.teach.models.student.*;
import org.fatmansoft.teach.models.system.StaticValue;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
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
import org.fatmansoft.teach.service.student.CourseService;
import org.fatmansoft.teach.service.student.GradeService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.xml.crypto.Data;
import java.util.*;

@Service
public class TeacherCourseService {

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

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

    @Autowired
    private StaticValueRepository staticValueRepository;

    public synchronized Integer getNewTeacherCourseId() {  //synchronized 同步方法
        Integer id = teacherCourseRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    /**
     * getMapFromTeacherCourse 将课程表属性数据转换复制MAp集合里
     */
    public Map getMapFromTeacherCourse(TeacherCourse tc) {
        Map m = new HashMap();
        if (tc == null) {
            return m;
        }
        m.put("courseId", tc.getCourse().getCourseId());
        m.put("courseName", tc.getCourse().getName());
        m.put("courseNum", tc.getCourse().getNum());
        if (tc.getCourse().getCampus() != null) {
            m.put("campusId", tc.getCourse().getCampus().getCampusId());
            m.put("campusName", tc.getCourse().getCampus().getName());
        }
        if (tc.getCourse().getGrade() != null) {
            m.put("gradeId", tc.getCourse().getGrade().getGradeId());
            m.put("gradeName", tc.getCourse().getGrade().getGradeName());
        }
        m.put("hour", tc.getCourse().getHour());
        m.put("type", tc.getCourse().getType());
        m.put("credit", tc.getCourse().getCredit());
        m.put("day", tc.getDay());
        m.put("timeOrder", tc.getTimeOrder());
        m.put("place", tc.getPlace());
        m.put("courseCapacity", tc.getCourseCapacity());
        m.put("selectedCount", tc.getSelectedCount());
        m.put("introduction", tc.getCourse().getIntroduction());
        m.put("teacherId", tc.getTeacher().getTeacherId());
        m.put("teacherName", tc.getTeacher().getPerson().getName());
        return m;
    }

    public DataResponse getMyCourses() {
        //获取当前用户（学生）的信息
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        List<TeacherCourse> sList = teacherCourseRepository.findTeacherCourseListByStudentId(studentId);  //数据库查询操作
        List dataList = TeacherCourseList(sList);
        return CommonMethod.getReturnData(dataList);
    }

    private List TeacherCourseList(List<TeacherCourse> sList) {
        List dataList = new ArrayList();
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTeacherCourse(sList.get(i)));
        }
        return dataList;
    }


    public DataResponse getMyTeacherCourses() {
        //获取当前用户（教师）的信息
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher teacher= sOp.get();
        Integer teacherId = teacher.getTeacherId();
        List<TeacherCourse> cList = teacherCourseRepository.findCourseListByTeacherId(teacherId);  //数据库查询操作
        List dataList = TeacherCourseList(cList);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCoursesByTeacherId(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Optional<Teacher> sOp= teacherRepository.findByUserId(teacherId);  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher teacher= sOp.get();
        List<TeacherCourse> cList = teacherCourseRepository.findCourseListByTeacherId(teacherId);  //数据库查询操作
        List dataList = TeacherCourseList(cList);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getByCourseNumName(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<TeacherCourse> cList = teacherCourseRepository.findCourseListByCourseNumName(numName);
        List dataList = TeacherCourseList(cList);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    public DataResponse addCourse(DataRequest dataRequest){
        Map map = dataRequest.getMap("form");
        String courseNum = CommonMethod.getString(map,"courseNum"); //课序号

        Course course = new Course();
        Integer courseId = courseService.getNewCourseId();
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

        Teacher teacher;
        String teacherNum = CommonMethod.getString(map,"teacherNum");
        Optional<Teacher> opTeacher = teacherRepository.findByPersonNum(teacherNum);
        if(opTeacher.isEmpty()){
            return CommonMethod.getReturnMessageError("教师不存在！请检查工号是否正确！");
        }else{
            teacher = opTeacher.get();
        }

        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setId(teacherCourseService.getNewTeacherCourseId());
        teacherCourse.setCourse(course);
        teacherCourse.setTeacher(teacher);
        teacherCourse.setDay(CommonMethod.getInteger(map,"day"));
        teacherCourse.setTimeOrder(CommonMethod.getInteger(map,"timeOrder"));
        teacherCourse.setPlace(CommonMethod.getString(map,"place"));
        teacherCourse.setSelectedCount(CommonMethod.getInteger(map,"selectedCount"));
        teacherCourse.setCourseCapacity(CommonMethod.getInteger(map,"courseCapacity"));
        teacherCourseRepository.saveAndFlush(teacherCourse);

        course.setNum(CommonMethod.getString(map,"courseNum"));
        course.setName(CommonMethod.getString(map,"courseName"));
        course.setHour(CommonMethod.getInteger(map,"hour"));
        course.setCredit(CommonMethod.getInteger(map,"credit"));
        course.setType(CommonMethod.getInteger(map,"courseType"));
        course.setIntroduction(CommonMethod.getString(map,"introduction"));
        courseRepository.save(course);

        return CommonMethod.getReturnData(teacherCourseService.getMapFromTeacherCourse(teacherCourse));

    }

    public DataResponse editCourse(DataRequest dataRequest) {
        Map map = dataRequest.getMap("form");
        String courseNum = CommonMethod.getString(map,"courseNum"); //课序号

        Course course = new Course();
        Integer courseId = courseService.getNewCourseId();
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

        Teacher teacher;
        String teacherNum = CommonMethod.getString(map,"teacherNum");
        Optional<Teacher> opTeacher = teacherRepository.findByPersonNum(teacherNum);
        if(opTeacher.isEmpty()){
            return CommonMethod.getReturnMessageError("教师不存在！请检查名字是否正确！");
        }else{
            teacher = opTeacher.get();
        }
        Integer teacherId = teacher.getTeacherId();
        TeacherCourse teacherCourse = null;
        Optional<TeacherCourse> opTeacherCourse = teacherCourseRepository.findByTeacherIdAndCourseId(teacherId, courseId);
        if(opTeacherCourse.isPresent()){
            teacherCourse = opTeacherCourse.get();
        }else{
            teacherCourse = new TeacherCourse();
            teacherCourse.setId(teacherCourseService.getNewTeacherCourseId());
            teacherCourse.setCourse(course);
            teacherCourse.setTeacher(teacher);
        }
        teacherCourse.setDay(CommonMethod.getInteger(map,"day"));
        teacherCourse.setTimeOrder(CommonMethod.getInteger(map,"timeOrder"));
        teacherCourse.setPlace(CommonMethod.getString(map,"place"));
        teacherCourse.setSelectedCount(CommonMethod.getInteger(map,"selectedCount"));
        teacherCourse.setCourseCapacity(CommonMethod.getInteger(map,"courseCapacity"));
        teacherCourseRepository.saveAndFlush(teacherCourse);

        course.setNum(CommonMethod.getString(map,"courseNum"));
        course.setName(CommonMethod.getString(map,"courseName"));
        course.setHour(CommonMethod.getInteger(map,"hour"));
        course.setCredit(CommonMethod.getInteger(map,"credit"));
        course.setType(CommonMethod.getInteger(map,"courseType"));
        course.setIntroduction(CommonMethod.getString(map,"introduction"));
        courseRepository.save(course);

        return CommonMethod.getReturnData(teacherCourseService.getMapFromTeacherCourse(teacherCourse));
    }


    public DataResponse getCoursesByType(DataRequest dataRequest) {
        Integer courseType = dataRequest.getInteger("courseType");
        List dataList = new ArrayList();
        List<TeacherCourse> sList = teacherCourseRepository.findCourseListByTypeId(courseType);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTeacherCourse(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse getCoursesByTeacherName(DataRequest dataRequest) {
        Integer teacherName = dataRequest.getInteger("teacherName");
        List dataList = new ArrayList();
        List<TeacherCourse> sList = teacherCourseRepository.findCourseListByteacherName(teacherName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTeacherCourse(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCoursesByDayTimeOrder(DataRequest dataRequest) {
        Integer day = dataRequest.getInteger("day");
        Integer timeOrder = dataRequest.getInteger("timeOrder");

        List dataList = new ArrayList();
        List<TeacherCourse> sList = null;
        if(day != null && timeOrder != null){
            sList = teacherCourseRepository.findCourseListByDayAndTimeOrder(day, timeOrder);  //数据库查询操作
        } else if(day != null && timeOrder == null){
            sList = teacherCourseRepository.findCourseListByDay(day);
        } else if(day == null && timeOrder != null){
            sList = teacherCourseRepository.findCourseListByTimeOrder(timeOrder);
        }

        if (sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTeacherCourse(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse selectCourse(DataRequest dataRequest) {
        StaticValue available = staticValueRepository.findById(Const.COURSE_SELECT_AVAILABLE).orElse(null);

        if(available.getValue().equals("1")){
            return CommonMethod.getReturnMessageError("选课未开启！");
        }
        Integer courseId = dataRequest.getInteger("courseId");
        Integer teacherId = dataRequest.getInteger("teacherId");

        TeacherCourse teacherCourse = null;
        Optional<TeacherCourse> optionalTeacherCourse = teacherCourseRepository.findByTeacherIdAndCourseId(teacherId,courseId);
        if(optionalTeacherCourse.isEmpty()){
            return CommonMethod.getReturnMessageError("课程不存在！");
        }else{
            teacherCourse = optionalTeacherCourse.get();
        }

        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();

        Integer day = teacherCourse.getDay();
        Integer timeOrder = teacherCourse.getTimeOrder();
        Optional<TeacherCourse> opSelectedCourse = teacherCourseRepository.findCourseListByStudentIdAndDayAndTimeOrder(day, timeOrder, studentId);
        if(opSelectedCourse.isPresent()){
            return CommonMethod.getReturnMessageError("与已选课程时间冲突！");
        }

        Integer selectedCount = teacherCourse.getSelectedCount();
        Integer courseCapacity = teacherCourse.getCourseCapacity();
        if(selectedCount >= courseCapacity){
            return CommonMethod.getReturnMessageError("课程无剩余容量，选课失败！");
        }

        teacherCourse.setSelectedCount(++selectedCount);
        teacherCourseRepository.save(teacherCourse);

        Score score = new Score();
        score.setTeacherCourse(teacherCourse);
        score.setStudent(student);
        score.setIsResult(0);
        scoreRepository.save(score);

        return CommonMethod.getReturnMessageOK("选课成功");
    }

    public DataResponse cancelCourse(DataRequest dataRequest) {
        if(Const.COURSE_SELECT_AVAILABLE.equals("1")){
            return CommonMethod.getReturnMessageError("选课未开启！");
        }

        Integer courseId = dataRequest.getInteger("courseId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        TeacherCourse teacherCourse = null;
        Optional<TeacherCourse> optionalTeacherCourse = teacherCourseRepository.findByTeacherIdAndCourseId(teacherId, courseId);
        if(optionalTeacherCourse.isEmpty()){
            return CommonMethod.getReturnMessageError("课程不存在！");
        }else{
            teacherCourse = optionalTeacherCourse.get();
        }

        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();

        Integer selectedCount = teacherCourse.getSelectedCount();
        Integer courseCapacity = teacherCourse.getCourseCapacity();

        teacherCourse.setSelectedCount(--selectedCount);
        teacherCourseRepository.save(teacherCourse);

        Optional<Score> optionalScore = scoreRepository.findByStudentIdAndCourseIdAndTeacherId(studentId, courseId, teacherId);
        if(optionalScore.isPresent()){
            Score score = optionalScore.get();
            scoreRepository.delete(score);
        }
        return CommonMethod.getReturnMessageOK("退课成功");
    }

    public DataResponse addStudentCourse(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        TeacherCourse teacherCourse = null;
        Optional<TeacherCourse> optionalTeacherCourse = teacherCourseRepository.findByTeacherIdAndCourseId(teacherId,courseId);
        if(optionalTeacherCourse.isEmpty()){
            return CommonMethod.getReturnMessageError("课程不存在！");
        }else{
            teacherCourse = optionalTeacherCourse.get();
        }

        Optional<Student> sOp= studentRepository.findByStudentId(studentId);  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();

        Integer selectedCount = teacherCourse.getSelectedCount();
        Integer courseCapacity = teacherCourse.getCourseCapacity();
        if(selectedCount >= courseCapacity){
            teacherCourse.setCourseCapacity(++courseCapacity);
        }

        teacherCourse.setSelectedCount(++selectedCount);
        teacherCourseRepository.save(teacherCourse);

        Score score = new Score();
        score.setTeacherCourse(teacherCourse);
        score.setStudent(student);
        score.setIsResult(0);
        scoreRepository.save(score);

        return CommonMethod.getReturnMessageOK("添加成功");
    }

    public DataResponse getMyAccessCourses() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        if(student.getClazz() == null){
            return CommonMethod.getReturnMessageError("学生无班级！");
        }
        Integer gradeId = student.getClazz().getGrade().getGradeId();
        List<TeacherCourse> cList = teacherCourseRepository.findTeacherCourseListByGradeGradeId(gradeId);  //数据库查询操作
        List dataList = new ArrayList();
        if(cList == null || cList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0; i < cList.size(); i++) {
            dataList.add(getMapFromTeacherCourse(cList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }
}

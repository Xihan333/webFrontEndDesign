package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.*;
import org.fatmansoft.teach.models.system.User;
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
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherService;
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
        m.put("courseCapacity",c.getCourseCapacity());
        m.put("selectedCount",c.getSelectedCount());
        m.put("introduction",c.getIntroduction());
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
            //先删除从表
            scoreRepository.deleteByCourseCourseId(courseId);
            teacherCourseRepository.deleteByCourseCourseId(courseId);
            //再删除主表
            courseRepository.deleteById(courseId);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getCoursesByType(DataRequest dataRequest) {
        Integer courseType = dataRequest.getInteger("courseType");
        List dataList = new ArrayList();
        List<Course> sList = courseRepository.findCourseListByTypeId(courseType);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCourse(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCoursesByTeacherName(DataRequest dataRequest) {
        Integer teacherName = dataRequest.getInteger("teacherName");
        List dataList = new ArrayList();
        List<Course> sList = courseRepository.findCourseListByteacherName(teacherName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCourse(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }


    public DataResponse getCourseOptionItemListByStudentId(DataRequest dataRequest) {
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
        return CommonMethod.getReturnData(itemList);
    }

    public DataResponse getCoursesByDayTimeOrder(DataRequest dataRequest) {
        Integer day = dataRequest.getInteger("day");
        Integer timeOrder = dataRequest.getInteger("timeOrder");

        List dataList = new ArrayList();
        List<Course> sList = null;
        if(day != null && timeOrder != null){
            sList = courseRepository.findCourseListByDayAndTimeOrder(day, timeOrder);  //数据库查询操作
        } else if(day != null && timeOrder == null){
            sList = courseRepository.findCourseListByDay(day);
        } else if(day == null && timeOrder != null){
            sList = courseRepository.findCourseListByTimeOrder(timeOrder);
        }

        if (sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromCourse(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse selectCourse(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> opC= courseRepository.findByCourseId(courseId);
        Course course = null;
        if(opC.isPresent()){
            course = opC.get();
        }else{
            return CommonMethod.getReturnMessageError("课程不存在！");
        }
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();

        Integer day = course.getDay();
        Integer timeOrder = course.getTimeOrder();
        Optional<Course> opSelectedCourse = courseRepository.findCourseListByStudentIdAndDayAndTimeOrder(day, timeOrder, studentId);
        if(opSelectedCourse.isPresent()){
            return CommonMethod.getReturnMessageError("与已选课程时间冲突！");
        }

        Integer selectedCount = course.getSelectedCount();
        Integer courseCapacity = course.getCourseCapacity();
        if(selectedCount >= courseCapacity){
            return CommonMethod.getReturnMessageError("课程无剩余容量，选课失败！");
        }

        course.setSelectedCount(++selectedCount);
        courseRepository.save(course);

        Score score = new Score();
        score.setCourse(course);
        score.setStudent(student);
        score.setIsResult(0);
        scoreRepository.save(score);

        return CommonMethod.getReturnMessageOK("选课成功");
    }

    public DataResponse addOrEditCourse(DataRequest dataRequest) {
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

        //修改开设学院
        Campus campus;
        Optional<Campus> opCampus;
        Integer campusId = dataRequest.getInteger("campusId");
        opCampus = campusRepository.findByCampusId(campusId);
        if(opCampus.isPresent()){
            campus = opCampus.get();
        }else{
            return CommonMethod.getReturnMessageError("学院不存在！");
        }
        course.setCampus(campus);

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
        course.setType(CommonMethod.getInteger(map,"courseType"));
        course.setSelectedCount(CommonMethod.getInteger(map,"selectedCount"));
        course.setCourseCapacity(CommonMethod.getInteger(map,"courseCapacity"));
        course.setIntroduction(CommonMethod.getString(map,"introduction"));
        courseRepository.save(course);

        return CommonMethod.getReturnMessageOK();
    }
}

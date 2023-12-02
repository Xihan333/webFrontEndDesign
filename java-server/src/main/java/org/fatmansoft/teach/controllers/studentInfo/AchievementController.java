package org.fatmansoft.teach.controllers.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.studentInfo.AchievementRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.TeacherService;
import org.fatmansoft.teach.service.studentInfo.AchievementService;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/achievement")
public class AchievementController {

    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private AchievementService achievementService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;


    public synchronized  Integer getNewAchievementId(){
        Integer id = achievementRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    /**
     *  getAchievementList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getAchievementList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAchievementList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = achievementService.getAchievementMapList(numName);
        System.out.println(dataList);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getStudentAchievement")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentAchievement(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer studentId = s.getStudentId();
        List dataList = achievementService.getAchievementMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeacherAchievement")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherAchievement(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Integer teacherId = s.getTeacherId();
        List dataList = achievementService.getAchievementMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/achievementDelete")
    public DataResponse achievementDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            achievementRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getAchievementInfo 前端点击成就列表时前端获取成就详细信息请求服务
     * @param dataRequest 从前端获取 achievementId 查询成就信息的主键 achievement_id
     * @return  根据achievementId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getAchievementInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAchievementInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(achievementService.getMapFromAchievement(a)); //这里回传包含成就信息的Map对象
    }

    /**
     * achievementEditSave 前端成就信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * achievementId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改成就的主键 achievement_id 返回前端
     */
    @PostMapping("/achievementEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String achievementName = CommonMethod.getString(form,"achievementName");  //Map 获取属性的值
        String level = CommonMethod.getString(form,"level");  //Map 获取属性的值
        String type = CommonMethod.getString(form,"type");  //Map 获取属性的值
        String content = CommonMethod.getString(form,"content");  //Map 获取属性的值
        String time = CommonMethod.getString(form,"time");  //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Student student = null;
        Optional<Teacher> t = teacherRepository.findByPersonNum(num);
        Teacher teacher = null;
        if(s.isPresent()){
            student = s.get();
        }else if(t.isPresent()){
            teacher = t.get();
        }else{
            return CommonMethod.getReturnMessageError("该学生或教师不存在，请输入正确姓名或学号");
        }
        Achievement a = null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            achievementId = getNewAchievementId(); //获取Achievement新的主键
            a = new Achievement();
            a.setAchievementId(achievementId);
        }
        a.setName(achievementName);
        a.setLevel(level);
        a.setContent(content);
        a.setType(type);
        a.setTime(time);
        a.setStatus(1);
        if(teacher == null){
            a.setStudent(student);
        }else{
            a.setTeacher(teacher);
        }
        achievementRepository.saveAndFlush(a);//插入新的Achievement记录
        return CommonMethod.getReturnData(a.getAchievementId());  // 将achievementId返回前端
    }

    @PostMapping("/achievementStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse achievementStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String achievementName = CommonMethod.getString(form,"achievementName");  //Map 获取属性的值
        String level = CommonMethod.getString(form,"level");  //Map 获取属性的值
        String type = CommonMethod.getString(form,"type");  //Map 获取属性的值
        String content = CommonMethod.getString(form,"content");  //Map 获取属性的值
        String time = CommonMethod.getString(form,"time");  //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Achievement a = null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            achievementId = getNewAchievementId(); //获取Achievement新的主键
            a = new Achievement();
            a.setAchievementId(achievementId);
        }
        a.setName(achievementName);
        a.setLevel(level);
        a.setContent(content);
        a.setType(type);
        a.setTime(time);
        a.setStudent(s);
        a.setStatus(0);
        achievementRepository.saveAndFlush(a);//插入新的Achievement记录
        return CommonMethod.getReturnData(a.getAchievementId());  // 将achievementId返回前端
    }

    @PostMapping("/achievementTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse achievementTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String achievementName = CommonMethod.getString(form,"achievementName");  //Map 获取属性的值
        String level = CommonMethod.getString(form,"level");  //Map 获取属性的值
        String type = CommonMethod.getString(form,"type");  //Map 获取属性的值
        String content = CommonMethod.getString(form,"content");  //Map 获取属性的值
        String time = CommonMethod.getString(form,"time");  //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Achievement a = null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            achievementId = getNewAchievementId(); //获取Achievement新的主键
            a = new Achievement();
            a.setAchievementId(achievementId);
        }
        a.setName(achievementName);
        a.setLevel(level);
        a.setContent(content);
        a.setType(type);
        a.setTime(time);
        a.setTeacher(s);
        a.setStatus(0);
        achievementRepository.saveAndFlush(a);//插入新的Achievement记录
        return CommonMethod.getReturnData(a.getAchievementId());  // 将achievementId返回前端
    }

    @PostMapping("/show/achievementWaiting")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = achievementService.getWaitingAchievementMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/achievementPassed")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementPassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = achievementService.getPassedAchievementMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/achievementFailed")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = achievementService.getFailedAchievementMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/achievementPass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementPass(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setStatus(1);
                achievementRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/achievementFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setStatus(2);
                achievementRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

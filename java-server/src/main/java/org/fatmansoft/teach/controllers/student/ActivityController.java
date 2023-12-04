package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Activity;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.student.ActivityRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.system.BaseService;
import org.fatmansoft.teach.service.student.StudentService;
import org.fatmansoft.teach.service.student.ActivityService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private StudentService studentService;
    @Autowired
    private ActivityRepository ActivityRepository;
    @Autowired
    private ActivityService ActivityService;

    public synchronized  Integer getNewActivityId(){
        Integer id = ActivityRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }
    @PostMapping("/getActivityList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest dataRequest) {
        String dayTitle= dataRequest.getString("dayTitle");
        List dataList = ActivityService.getActivityMapList(dayTitle);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getByStudentId")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        List<Activity> dataList = ActivityRepository.findActivityByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取当前用户（学生）的活动
     * @param dataRequest
     * @return
     */
    @PostMapping("/getStudentActivity")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentActivity(@Valid @RequestBody DataRequest dataRequest){
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("该用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp = studentRepository.findByPersonPersonId(u.getUserId());
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s = sOp.get();
        Integer studentId = s.getStudentId();
        List dataList = ActivityService.getActivityMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/ActivityDelete")
    public DataResponse ActivityDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");  //获取activity_id值
        Activity a= null;
        Optional<Activity> op;
        if(activityId != null) {
            op= ActivityRepository.findById(activityId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            ActivityRepository.delete(a);//删除该条体育活动
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getActivityInfo 前端点击体育活动列表时前端获取旅程详细信息请求服务
     * @param dataRequest 从前端获取 activityId 查询体育活动信息的主键 activity_id
     * @return  根据activityId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getActivityInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivityInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");  //获取activity_id值
        Activity a= null;
        Optional<Activity> op;
        if(activityId != null) {
            op= ActivityRepository.findById(activityId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(ActivityService.getMapFromActivity(a)); //这里回传包含体育活动信息的Map对象
    }

    /**
     * ActivityEditSave 前端旅程信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * activityId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 activity_id 返回前端
     */
    @PostMapping("/ActivityEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse ActivityEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");  //获取activity_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String title = CommonMethod.getString(form,"title");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");//Map 获取属性的值
        String introduction = CommonMethod.getString(form,"introduction"); //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Optional<Student> s1 = studentRepository.findByPersonName(name);
        Student student = null;
        if(s.isPresent()){
            student = s.get();
        }else if(s1.isPresent()){
            student = s1.get();
        }else{
            return CommonMethod.getReturnMessageError("该学生不存在，请输入正确的姓名或学号");
        }
        Activity a = null;
        Optional<Activity> op;
        if(activityId != null) {
            op= ActivityRepository.findById(activityId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            activityId = getNewActivityId(); //获取Activity新的主键
            a = new Activity();
            a.setActivityId(activityId);
        }
        a.setDay(day);
        a.setTitle(title);
        a.setLocation(location);
        a.setIntroduction(introduction);
        a.setStudent(student);
        ActivityRepository.saveAndFlush(a);//插入新的activity记录
        return CommonMethod.getReturnData(a.getActivityId());  // 将activityId返回前端
    }

    @PostMapping("/ActivityStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse ActivityStudentEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer activityId = dataRequest.getInteger("activityId");  //获取activity_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String title = CommonMethod.getString(form,"title");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");//Map 获取属性的值
        String introduction = CommonMethod.getString(form,"introduction"); //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp = studentRepository.findByPersonPersonId(u.getUserId());
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s = sOp.get();
        Activity a = null;
        Optional<Activity> op;
        if(activityId != null) {
            op= ActivityRepository.findById(activityId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            activityId = getNewActivityId(); //获取Activity新的主键
            a = new Activity();
            a.setActivityId(activityId);
        }
        a.setDay(day);
        a.setTitle(title);
        a.setLocation(location);
        a.setIntroduction(introduction);
        a.setStudent(s);
        ActivityRepository.saveAndFlush(a);//插入新的activity记录
        return CommonMethod.getReturnData(a.getActivityId());
    }
}

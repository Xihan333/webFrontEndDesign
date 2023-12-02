package org.fatmansoft.teach.controllers.dailyActivity;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.LiteraturePerformance;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.dailyActivity.LiteraturePerformanceRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.service.dailyActivity.LiteraturePerformanceService;
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
@RequestMapping("/api/literaturePerformance")

public class LiteraturePerformanceController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private StudentService studentService;
    @Autowired
    private LiteraturePerformanceRepository literaturePerformanceRepository;
    @Autowired
    private LiteraturePerformanceService literaturePerformanceService;

    public synchronized  Integer getNewPerformanceId(){
        Integer id = literaturePerformanceRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getLiteraturePerformanceList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getLiteraturePerformanceList(@Valid @RequestBody DataRequest dataRequest) {
        String day= dataRequest.getString("day");
        List dataList = literaturePerformanceService.getLiteraturePerformanceMapList(day);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getByStudentId")
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        List<LiteraturePerformance> dataList = literaturePerformanceRepository.findLiteraturePerformanceByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getStudentLiteraturePerformance")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentLiteraturePerformance(@Valid @RequestBody DataRequest dataRequest){
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
        List dataList = literaturePerformanceService.getLiteraturePerformanceByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/literaturePerformanceDelete")
    public DataResponse literaturePerformanceDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer performanceId = dataRequest.getInteger("performanceId");  //获取performance_id值
        LiteraturePerformance a= null;
        Optional<LiteraturePerformance> op;
        if(performanceId != null) {
            op= literaturePerformanceRepository.findById(performanceId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            literaturePerformanceRepository.delete(a);//删除该条处分
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getLiteraturePerformanceInfo 前端点击文艺汇演列表时前端获取处分详细信息请求服务
     * @param dataRequest 从前端获取 performanceId 查询文艺汇演信息的主键 performance_id
     * @return  根据performanceId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getLiteraturePerformanceInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getLiteraturePerformanceInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer performanceId = dataRequest.getInteger("performanceId");  //获取performance_id值
        LiteraturePerformance a= null;
        Optional<LiteraturePerformance> op;
        if(performanceId != null) {
            op= literaturePerformanceRepository.findById(performanceId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(literaturePerformanceService.getMapFromLiteraturePerformance(a)); //这里回传包含文艺汇演信息的Map对象
    }

    /**
     * LiteraturePerformanceEditSave 前端处分信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * performanceId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改处分的主键 performance_id 返回前端
     */
    @PostMapping("/literaturePerformanceEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse literaturePerformanceEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer performanceId = dataRequest.getInteger("performanceId");  //获取performance_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");  //Map 获取属性的值
        String programme = CommonMethod.getString(form,"programme");  //Map 获取属性的值
        String performanceType = CommonMethod.getString(form,"performanceType"); //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Student student = null;
        if(s.isPresent()){
            student = s.get();
        }else{
            return CommonMethod.getReturnMessageError("该学生不存在，请输入正确的姓名或学号");
        }
        LiteraturePerformance a = null;
        Optional<LiteraturePerformance> op;
        if(performanceId != null) {
            op= literaturePerformanceRepository.findById(performanceId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            performanceId = getNewPerformanceId(); //获取LiteraturePerformance新的主键
            a = new LiteraturePerformance();
            a.setPerformanceId(performanceId);
        }
        a.setDay(day);
        a.setLocation(location);
        a.setProgramme(programme);
        a.setPerformanceType(performanceType);
        a.setStudent(student);
        literaturePerformanceRepository.saveAndFlush(a);//插入新的LiteraturePerformance记录
        return CommonMethod.getReturnData(a.getPerformanceId());  // 将performanceId返回前端
    }

    @PostMapping("/literaturePerformanceStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse literaturePerformanceStudentEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer performanceId = dataRequest.getInteger("performanceId");  //获取performance_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");  //Map 获取属性的值
        String programme = CommonMethod.getString(form,"programme");  //Map 获取属性的值
        String performanceType = CommonMethod.getString(form,"performanceType"); //Map 获取属性的值
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
        LiteraturePerformance a = null;
        Optional<LiteraturePerformance> op;
        if(performanceId != null) {
            op= literaturePerformanceRepository.findById(performanceId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            performanceId = getNewPerformanceId(); //获取LiteraturePerformance新的主键
            a = new LiteraturePerformance();
            a.setPerformanceId(performanceId);
        }
        a.setDay(day);
        a.setLocation(location);
        a.setProgramme(programme);
        a.setPerformanceType(performanceType);
        a.setStudent(s);
        literaturePerformanceRepository.saveAndFlush(a);//插入新的LiteraturePerformance记录
        return CommonMethod.getReturnData(a.getPerformanceId());
    }
}

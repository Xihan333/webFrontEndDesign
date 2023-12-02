package org.fatmansoft.teach.controllers.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.studentInfo.Punishment;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.studentInfo.PunishmentRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.studentInfo.PunishmentService;
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
@RequestMapping("/api/punishment")
public class PunishmentController {
    @Autowired
    private PunishmentRepository punishmentRepository;
    @Autowired
    private PunishmentService punishmentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService;

    public synchronized  Integer getNewPunishmentId(){
        Integer id = punishmentRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getPunishmentList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getPunishmentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = punishmentService.getPunishmentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getStudentPunishment")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentPunishment(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = punishmentService.getPunishmentMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/punishmentDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse punishmentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer punishmentId = dataRequest.getInteger("punishmentId");  //获取punishment_id值
        Punishment a= null;
        Optional<Punishment> op;
        if(punishmentId != null) {
            op= punishmentRepository.findById(punishmentId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            punishmentRepository.delete(a);//删除该条处分
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getPunishmentInfo 前端点击处分列表时前端获取处分详细信息请求服务
     * @param dataRequest 从前端获取 punishmentId 查询处分信息的主键 punishment_id
     * @return  根据punishmentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getPunishmentInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer punishmentId = dataRequest.getInteger("punishmentId");  //获取punishment_id值
        Punishment a= null;
        Optional<Punishment> op;
        if(punishmentId != null) {
            op= punishmentRepository.findById(punishmentId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(punishmentService.getMapFromPunishment(a)); //这里回传包含处分信息的Map对象
    }

    /**
     * punishmentEditSave 前端处分信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * punishmentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改处分的主键 punishment_id 返回前端
     */
    @PostMapping("/punishmentEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer punishmentId = dataRequest.getInteger("punishmentId");  //获取punishment_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String punishmentName = CommonMethod.getString(form,"punishmentName");  //Map 获取属性的值
        String content = CommonMethod.getString(form,"content");  //Map 获取属性的值
        String time = CommonMethod.getString(form,"time");  //Map 获取属性的值
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
            return CommonMethod.getReturnMessageError("该学生不存在，请输入正确姓名或学号");
        }
        Punishment a = null;
        Optional<Punishment> op;
        if(punishmentId != null) {
            op= punishmentRepository.findById(punishmentId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            punishmentId = getNewPunishmentId(); //获取Punishment新的主键
            a = new Punishment();
            a.setPunishmentId(punishmentId);
        }
        a.setName(punishmentName);
        a.setContent(content);
        a.setTime(time);
        a.setStudent(student);
        punishmentRepository.saveAndFlush(a);//插入新的Punishment记录
        return CommonMethod.getReturnData(a.getPunishmentId());  // 将punishmentId返回前端
    }
}

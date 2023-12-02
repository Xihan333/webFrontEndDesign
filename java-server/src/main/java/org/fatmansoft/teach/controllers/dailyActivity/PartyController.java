package org.fatmansoft.teach.controllers.dailyActivity;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Party;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.dailyActivity.PartyRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.service.dailyActivity.PartyService;
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
@RequestMapping("/api/party")

public class PartyController {
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
    private PartyRepository partyRepository;
    @Autowired
    private PartyService partyService;

    public synchronized  Integer getNewPartyId(){
        Integer id = partyRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getPartyList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getPartyList(@Valid @RequestBody DataRequest dataRequest) {
        String organizerLocation = dataRequest.getString("organizerLocation");
        List dataList = partyService.getPartyMapList(organizerLocation);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getByStudentId")
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        List<Party> dataList = partyRepository.findPartyByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getStudentParty")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentParty(@Valid @RequestBody DataRequest dataRequest){
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
        List dataList = partyService.getPartyMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/partyDelete")
    public DataResponse partyDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer partyId = dataRequest.getInteger("partyId");  //获取party_id值
        Party a= null;
        Optional<Party> op;
        if(partyId != null) {
            op= partyRepository.findById(partyId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            partyRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getPartyInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getPartyInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer partyId = dataRequest.getInteger("partyId");  //获取party_id值
        Party a= null;
        Optional<Party> op;
        if(partyId != null) {
            op= partyRepository.findById(partyId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(partyService.getMapFromParty(a)); //这里回传包含聚会信息的Map对象
    }

    @PostMapping("/partyEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse partyEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer partyId = dataRequest.getInteger("partyId");  //获取party_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String title = CommonMethod.getString(form,"title");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");//Map 获取属性的值
        String organizer = CommonMethod.getString(form,"organizer");//Map 获取属性的值
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
        Party a = null;
        Optional<Party> op;
        if(partyId != null) {
            op= partyRepository.findById(partyId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            partyId = getNewPartyId(); //获取Party新的主键
            a = new Party();
            a.setPartyId(partyId);
        }
        a.setDay(day);
        a.setTitle(title);
        a.setLocation(location);
        a.setOrganizer(organizer);
        a.setIntroduction(introduction);
        a.setStudent(student);
        partyRepository.saveAndFlush(a);//插入新的party记录
        return CommonMethod.getReturnData(a.getPartyId());  // 将partyId返回前端
    }
    @PostMapping("/partyStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse partyStudentEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer partyId = dataRequest.getInteger("partyId");
        Map form = dataRequest.getMap("form");
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String title = CommonMethod.getString(form,"title");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");//Map 获取属性的值
        String organizer = CommonMethod.getString(form,"organizer");//Map 获取属性的值
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
        Party a = null;
        Optional<Party> op;
        if(partyId != null){
            op = partyRepository.findById(partyId);
            if(op.isPresent()){
                a = op.get();
            }
        }
        if(a == null){
            partyId = getNewPartyId();
            a = new Party();
            a.setPartyId(partyId);
        }
        a.setDay(day);
        a.setTitle(title);
        a.setLocation(location);
        a.setOrganizer(organizer);
        a.setIntroduction(introduction);
        a.setStudent(s);
        partyRepository.saveAndFlush(a);//插入新的party记录
        return CommonMethod.getReturnData(a.getPartyId());
    }
}

package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Message;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Party;
import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.FeeRepository;
import org.fatmansoft.teach.repository.MessageRepository;
import org.fatmansoft.teach.repository.study.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.MessageService;
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
@RequestMapping("/api/message")

public class MessageController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private MessageRepository messageRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MessageService messageService;

    public synchronized Integer getNewMessageId() {
        Integer id = messageRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/getMessageList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getMessageList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = messageService.getMessageMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getByStudentId")
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        List<Message> dataList = messageRepository.findMessageByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getStudentMessage")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentMessage(@Valid @RequestBody DataRequest dataRequest){
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
        List dataList = messageService.getMessageMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/messageDelete")
    public DataResponse messageDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer messageId = dataRequest.getInteger("messageId");
        Message s = null;
        Optional<Message> op;
        if (messageId != null) {
            op = messageRepository.findById(messageId);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            messageRepository.delete(s);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/getMessageInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getMessageInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer messageId = dataRequest.getInteger("messageId");
        Message s = null;
        Optional<Message> op;
        if (messageId != null) {
            op = messageRepository.findById(messageId);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(messageService.getMapFromMessage(s));
    }

    @PostMapping("/messageEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse messageEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer messageId = dataRequest.getInteger("messageId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String place = CommonMethod.getString(form,"place");  //Map 获取属性的值
        String start = CommonMethod.getString(form,"start");  //Map 获取属性的值
        String end = CommonMethod.getString(form,"end");//Map 获取属性的值
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
        Message a = null;
        Optional<Message> op;
        if(messageId != null) {
            op= messageRepository.findById(messageId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            messageId = getNewMessageId();
            a = new Message();
            a.setMessageId(messageId);
        }
        a.setPlace(place);
        a.setStart(start);
        a.setEnd(end);
        a.setStudent(student);
        a.setStatus(0);
        messageRepository.saveAndFlush(a);
        return CommonMethod.getReturnData(a.getMessageId());
    }
    @PostMapping("/messageStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse messageStudentEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer messageId = dataRequest.getInteger("messageId");
        Map form = dataRequest.getMap("form");
        String place = CommonMethod.getString(form,"place");  //Map 获取属性的值
        String start = CommonMethod.getString(form,"start");  //Map 获取属性的值
        String end = CommonMethod.getString(form,"end");//Map 获取属性的值
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
        Message a = null;
        Optional<Message> op;
        if(messageId != null){
            op = messageRepository.findById(messageId);
            if(op.isPresent()){
                a = op.get();
            }
        }
        if(a == null){
            messageId = getNewMessageId();
            a = new Message();
            a.setMessageId(messageId);
        }
        a.setPlace(place);
        a.setStart(start);
        a.setEnd(end);
        a.setStudent(s);
        int v=0;
        a.setStatus(v);
        messageRepository.saveAndFlush(a);
        return CommonMethod.getReturnData(a.getMessageId());
    }

    @PostMapping("/show/messageWaiting")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse messageWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = messageService.getWaitingMessageMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/messagePassed")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse messagePassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = messageService.getPassedMessageMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/messageFailed")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse messageFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = messageService.getFailedMessageMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/messagePass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse messagePass(@Valid @RequestBody DataRequest dataRequest) {
        Integer messageId = dataRequest.getInteger("messageId");  //获取achievement_id值
        Message a= null;
        Optional<Message> op;
        if(messageId != null) {
            op= messageRepository.findById(messageId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setStatus(1);
                messageRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/messageFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse messageFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer messageId = dataRequest.getInteger("messageId");  //获取achievement_id值
        Message a= null;
        Optional<Message> op;
        if(messageId != null) {
            op= messageRepository.findById(messageId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setStatus(2);
                messageRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}



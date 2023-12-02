package org.fatmansoft.teach.controllers;

import lombok.extern.slf4j.Slf4j;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Message;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.FeeRepository;
import org.fatmansoft.teach.repository.study.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.FeeService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/fee")

public class FeeController {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private BaseService baseService;
    @Autowired
    private FeeService feeService;

    public synchronized Integer getNewFeeId() {
        Integer id = feeRepository.getMaxId();
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/getFeeList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getFeeList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = feeService.getFeeMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getByStudentId")
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        List<Fee> dataList = feeRepository.findFeeByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getStudentFee")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentFee(@Valid @RequestBody DataRequest dataRequest){
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
        List dataList = feeService.getFeeMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/feeDelete")
    public DataResponse feeDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        //log.error(String.valueOf(feeId));
        Fee s = null;
        Optional<Fee> op;
        if (feeId != null) {
            op = feeRepository.findById(feeId);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            feeRepository.delete(s);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/getFeeInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getFeeInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Fee s = null;
        Optional<Fee> op;
        if (feeId != null) {
            op = feeRepository.findById(feeId);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(feeService.getMapFromFee(s));
    }
    @PostMapping("/feeGet")
    public DataResponse getFee(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId=dataRequest.getInteger("studentId");
        String date=dataRequest.getString("date");
        double fee=feeRepository.getMoneyByStudentIdAndDate(studentId,date);
        return CommonMethod.getReturnData(fee);
    }

    @PostMapping("/feeEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse feeEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Double money = CommonMethod.getDouble(form,"money");  //Map 获取属性的值
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
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
        Fee a = null;
        Optional<Fee> op;
        if(feeId != null) {
            op= feeRepository.findById(feeId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            feeId = getNewFeeId();
            a = new Fee();
            a.setFeeId(feeId);
        }
        a.setMoney(money);
        a.setDay(day);
        a.setStudent(student);
        feeRepository.saveAndFlush(a);
        return CommonMethod.getReturnData(a.getFeeId());
    }
    @PostMapping("/feeStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse feeStudentEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer feeId = dataRequest.getInteger("feeId");
        Map form = dataRequest.getMap("form");
        Double money = CommonMethod.getDouble(form,"money");  //Map 获取属性的值
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
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
        Fee a = null;
        Optional<Fee> op;
        if(feeId != null){
            op = feeRepository.findById(feeId);
            if(op.isPresent()){
                a = op.get();
            }
        }
        if(a == null){
            feeId = getNewFeeId();
            a = new Fee();
            a.setFeeId(feeId);
        }
        a.setMoney(money);
        a.setDay(day);
        a.setStudent(s);
        feeRepository.saveAndFlush(a);
        return CommonMethod.getReturnData(a.getFeeId());
    }
}

package org.fatmansoft.teach.controllers.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.Internship;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.creativePratice.InternshipRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.creativePractice.InternshipService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/internship")
public class InternshipController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InternshipRepository internshipRepository;
    @Autowired
    private InternshipService internshipService;

    public synchronized  Integer getNewInternshipId(){
        Integer id = internshipRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getInternshipList")
    public DataResponse geInternshipList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = internshipService.getInternshipMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/internshipDelete")
    public DataResponse internshipDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");  //获取internship_id值
        Internship a= null;
        Optional<Internship> op;
        if(internshipId != null) {
            op= internshipRepository.findById(internshipId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            internshipRepository.delete(a);//删除该条实习信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getInternshipInfo 前端点击实习信息列表时前端获取详细信息请求服务
     * @param dataRequest 从前端获取 internshipId 查询旅程信息的主键 internship_id
     * @return  根据internshipId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getInternshipInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getInternshipInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");  //获取internship_id值
        Internship a= null;
        Optional<Internship> op;
        if(internshipId != null) {
            op= internshipRepository.findById(internshipId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(internshipService.getMapFromInternship(a)); //这里回传包含聚会信息的Map对象
    }

    /**
     * InternshipEditSave 前端实习信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * internshipId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 internship_id 返回前端
     */

    @PostMapping("/internshipEditSave")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");  //获取internship_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String unit = CommonMethod.getString(form,"unit");  //Map 获取属性的值
        String lastTime = CommonMethod.getString(form,"lastTime");  //Map 获取属性的值
        String post = CommonMethod.getString(form,"post");  //Map 获取属性的值
        String certificate = CommonMethod.getString(form,"certificate");//Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Student student = null;
        if(s.isPresent()){
            student = s.get();
        }else{
            return CommonMethod.getReturnMessageError("该学生不存在，请输入正确姓名或学号");
        }
        Internship a = null;
        Optional<Internship> op;
        if(internshipId != null) {
            op= internshipRepository.findById(internshipId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            internshipId = getNewInternshipId(); //获取Internship新的主键
            a = new Internship();
            a.setInternshipId(internshipId);
        }
        a.setUnit(unit);
        a.setLastTime(lastTime);
        a.setPost(post);
        a.setStudent(student);
        a.setCertificate(certificate);
        a.setAuditStatus(1);
        internshipRepository.saveAndFlush(a);//插入新的internship记录
        return CommonMethod.getReturnData(a.getInternshipId());  // 将internshipId返回前端
    }
    @PostMapping("/internshipStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse internshipStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");  //获取internship_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String unit = CommonMethod.getString(form,"unit");  //Map 获取属性的值
        String lastTime = CommonMethod.getString(form,"lastTime");  //Map 获取属性的值
        String post = CommonMethod.getString(form,"post");  //Map 获取属性的值
        String certificate = CommonMethod.getString(form,"certificate");//Map 获取属性的值
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
        Internship a = null;
        Optional<Internship> op;
        if(internshipId != null) {
            op= internshipRepository.findById(internshipId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            internshipId = getNewInternshipId(); //获取Internship新的主键
            a = new Internship();
            a.setInternshipId(internshipId);
        }
        a.setUnit(unit);
        a.setLastTime(lastTime);
        a.setPost(post);
        a.setCertificate(certificate);
        a.setStudent(s);
        a.setAuditStatus(0);
        internshipRepository.saveAndFlush(a);//插入新的Internship记录
        return CommonMethod.getReturnData(a.getInternshipId());  // 将internshipId返回前端
    }

    @PostMapping("/show/internshipWaiting")
    public DataResponse internshipWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = internshipService.getWaitingInternshipMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/internshipPassed")
    public DataResponse internshipPassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = internshipService.getPassedInternshipMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/internshipFailed")
    public DataResponse internshipFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = internshipService.getFailedInternshipMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/internshipPass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse internshipPass(@Valid @RequestBody DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");  //获取internship_id值
        System.out.println(internshipId);
        Internship a= null;
        Optional<Internship> op;
        if(internshipId != null) {
            op= internshipRepository.findById(internshipId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(1);
                internshipRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/internshipFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse internshipFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("internshipId");  //获取internship_id值
        Internship a= null;
        Optional<Internship> op;
        if(internshipId != null) {
            op= internshipRepository.findById(internshipId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(2);
                internshipRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getStudentInternship")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentInternship(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = internshipService.getInternshipMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
}

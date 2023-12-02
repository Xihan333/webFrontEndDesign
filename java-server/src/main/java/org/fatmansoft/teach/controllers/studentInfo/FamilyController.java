package org.fatmansoft.teach.controllers.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.studentInfo.Family;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.studentInfo.FamilyRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.TeacherService;
import org.fatmansoft.teach.service.studentInfo.FamilyService;
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
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    FamilyRepository familyRepository;

    @Autowired
    FamilyService familyService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewFamilyId(){
        Integer id = familyRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getStudentFamilyList")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentFamilyList(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = familyService.getFamilyMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeacherFamilyList")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherFamilyList(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Integer teacherId = s.getTeacherId();
        List dataList = familyService.getFamilyMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/familyDelete")
    public DataResponse familyDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer familyId = dataRequest.getInteger("familyId");  //获取family_id值
        Family a= null;
        Optional<Family> op;
        if(familyId != null) {
            op= familyRepository.findById(familyId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            familyRepository.delete(a);//删除该成员
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/familyStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse familyStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        System.out.println(uOp);
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer familyId = dataRequest.getInteger("familyId");  //获取family_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String familyName = CommonMethod.getString(form,"familyName");  //Map 获取属性的值
        String familyGender = CommonMethod.getString(form,"familyGender");  //Map 获取属性的值
        String familyBirthday = CommonMethod.getString(form,"familyBirthday");  //Map 获取属性的值
        String relation = CommonMethod.getString(form,"relation");  //Map 获取属性的值
        String description = CommonMethod.getString(form,"description");  //Map 获取属性的值
        Family a = null;
        Optional<Family> op;
        if(familyId != null) {
            op= familyRepository.findById(familyId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            familyId = getNewFamilyId(); //获取Family新的主键
            a = new Family();
            a.setFamilyId(familyId);
        }
        a.setName(familyName);
        a.setGender(familyGender);
        a.setBirthday(familyBirthday);
        a.setDescription(description);
        a.setRelation(relation);
        a.setStudent(s);
        familyRepository.saveAndFlush(a);//插入新的Family记录
        return CommonMethod.getReturnData(a.getFamilyId());  // 将familyId返回前端
    }
    @PostMapping("/familyTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse familyTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        System.out.println(uOp);
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Integer familyId = dataRequest.getInteger("familyId");  //获取family_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String familyName = CommonMethod.getString(form,"familyName");  //Map 获取属性的值
        String familyGender = CommonMethod.getString(form,"familyGender");  //Map 获取属性的值
        String familyBirthday = CommonMethod.getString(form,"familyBirthday");  //Map 获取属性的值
        String relation = CommonMethod.getString(form,"relation");  //Map 获取属性的值
        String description = CommonMethod.getString(form,"description");  //Map 获取属性的值
        Family a = null;
        Optional<Family> op;
        if(familyId != null) {
            op= familyRepository.findById(familyId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            familyId = getNewFamilyId(); //获取Family新的主键
            a = new Family();
            a.setFamilyId(familyId);
        }
        a.setName(familyName);
        a.setGender(familyGender);
        a.setBirthday(familyBirthday);
        a.setDescription(description);
        a.setRelation(relation);
        a.setTeacher(s);
        familyRepository.saveAndFlush(a);//插入新的Family记录
        return CommonMethod.getReturnData(a.getFamilyId());  // 将familyId返回前端
    }
}

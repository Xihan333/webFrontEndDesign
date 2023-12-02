package org.fatmansoft.teach.controllers.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.studentInfo.SocialRelation;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.studentInfo.SocialRelationRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.TeacherService;
import org.fatmansoft.teach.service.studentInfo.SocialRelationService;
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
@RequestMapping("/api/socialRelation")
public class SocialRelationController {

    @Autowired
    SocialRelationRepository socialRelationRepository;

    @Autowired
    SocialRelationService socialRelationService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewSocialRelationId(){
        Integer id = socialRelationRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getStudentSocialRelationList")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentSocialRelationList(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = socialRelationService.getSocialRelationMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeacherSocialRelationList")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherSocialRelationList(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList =socialRelationService.getSocialRelationMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/socialRelationDelete")
    public DataResponse socialRelationDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialRelationId = dataRequest.getInteger("socialRelationId");  //获取socialRelation_id值
        SocialRelation a= null;
        Optional<SocialRelation> op;
        if(socialRelationId != null) {
            op= socialRelationRepository.findById(socialRelationId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            socialRelationRepository.delete(a);//删除该成员
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/socialRelationStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse socialRelationStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
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
        Integer socialRelationId = dataRequest.getInteger("socialRelationId");  //获取socialRelation_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String socialRelationName = CommonMethod.getString(form,"socialRelationName");  //Map 获取属性的值
        String socialRelationGender = CommonMethod.getString(form,"socialRelationGender");  //Map 获取属性的值
        String socialRelationBirthday = CommonMethod.getString(form,"socialRelationBirthday");  //Map 获取属性的值
        String relation = CommonMethod.getString(form,"relation");  //Map 获取属性的值
        String description = CommonMethod.getString(form,"socialRelationDescription");  //Map 获取属性的值
        SocialRelation a = null;
        Optional<SocialRelation> op;
        if(socialRelationId != null) {
            op= socialRelationRepository.findById(socialRelationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            socialRelationId = getNewSocialRelationId(); //获取SocialRelation新的主键
            a = new SocialRelation();
            a.setSocialRelationId(socialRelationId);
        }
        a.setName(socialRelationName);
        a.setGender(socialRelationGender);
        a.setBirthday(socialRelationBirthday);
        a.setDescription(description);
        a.setRelation(relation);
        a.setStudent(s);
        socialRelationRepository.saveAndFlush(a);//插入新的SocialRelation记录
        return CommonMethod.getReturnData(a.getSocialRelationId());  // 将socialRelationId返回前端
    }

    @PostMapping("/socialRelationTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse socialRelationTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
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
        Integer socialRelationId = dataRequest.getInteger("socialRelationId");  //获取socialRelation_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String socialRelationName = CommonMethod.getString(form,"socialRelationName");  //Map 获取属性的值
        String socialRelationGender = CommonMethod.getString(form,"socialRelationGender");  //Map 获取属性的值
        String socialRelationBirthday = CommonMethod.getString(form,"socialRelationBirthday");  //Map 获取属性的值
        String relation = CommonMethod.getString(form,"relation");  //Map 获取属性的值
        String description = CommonMethod.getString(form,"socialRelationDescription");  //Map 获取属性的值
        SocialRelation a = null;
        Optional<SocialRelation> op;
        if(socialRelationId != null) {
            op= socialRelationRepository.findById(socialRelationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            socialRelationId = getNewSocialRelationId(); //获取SocialRelation新的主键
            a = new SocialRelation();
            a.setSocialRelationId(socialRelationId);
        }
        a.setName(socialRelationName);
        a.setGender(socialRelationGender);
        a.setBirthday(socialRelationBirthday);
        a.setDescription(description);
        a.setRelation(relation);
        a.setTeacher(s);
        socialRelationRepository.saveAndFlush(a);//插入新的SocialRelation记录
        return CommonMethod.getReturnData(a.getSocialRelationId());  // 将socialRelationId返回前端
    }
}

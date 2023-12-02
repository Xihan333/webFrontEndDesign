package org.fatmansoft.teach.controllers.studentInfo;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.studentInfo.EducationExperience;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.studentInfo.EducationExperienceRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.TeacherService;
import org.fatmansoft.teach.service.studentInfo.EducationExperienceService;
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
@RequestMapping("/api/educationExperience")
public class EducationExperienceController {
    @Autowired
    private EducationExperienceRepository educationExperienceRepository;

    @Autowired
    private EducationExperienceService educationExperienceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewEducationExperienceId(){
        Integer id = educationExperienceRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }
    @PostMapping("/getStudentEducationExperienceList")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentEducationExperienceList(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = educationExperienceService.getStudentEducationExperienceMapList(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeacherEducationExperienceList")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherEducationExperienceList(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = educationExperienceService.getTeacherEducationExperienceMapList(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/educationExperienceDelete")
    public DataResponse educationExperienceDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer educationExperienceId = dataRequest.getInteger("educationExperienceId");  //获取educationExperience_id值
        EducationExperience a= null;
        Optional<EducationExperience> op;
        if(educationExperienceId != null) {
            op= educationExperienceRepository.findById(educationExperienceId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            educationExperienceRepository.delete(a);//删除该经历
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/educationExperienceStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse educationExperienceStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer educationExperienceId = dataRequest.getInteger("educationExperienceId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String name = CommonMethod.getString(form,"educationExperienceName");  //Map 获取属性的值
        String startTime = CommonMethod.getString(form,"startTime");  //Map 获取属性的值
        String endTime = CommonMethod.getString(form,"endTime");  //Map 获取属性的值
        String level = CommonMethod.getString(form,"level");  //Map 获取属性的值
        String teacher = CommonMethod.getString(form,"teacher");  //Map 获取属性的值
        String description = CommonMethod.getString(form,"Edescription");  //Map 获取属性的值
        String position = CommonMethod.getString(form,"position");  //Map 获取属性的值
        EducationExperience a = null;
        Optional<EducationExperience> op;
        if(educationExperienceId != null) {
            op= educationExperienceRepository.findById(educationExperienceId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            educationExperienceId = getNewEducationExperienceId(); //获取EducationExperience新的主键
            a = new EducationExperience();
            a.setEducationExperienceId(educationExperienceId);
        }
        a.setName(name);
        a.setStartTime(startTime);
        a.setEndTime(endTime);
        a.setLevel(level);
        a.setProofTeacher(teacher);
        a.setDescription(description);
        a.setPosition(position);
        a.setStudent(s);
        educationExperienceRepository.saveAndFlush(a);//插入新的EducationExperience记录
        return CommonMethod.getReturnData(a.getEducationExperienceId());  // 将educationExperienceId返回前端
    }
    @PostMapping("/educationExperienceTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse educationExperienceTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Integer educationExperienceId = dataRequest.getInteger("educationExperienceId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String name = CommonMethod.getString(form,"educationExperienceName");  //Map 获取属性的值
        String startTime = CommonMethod.getString(form,"startTime");  //Map 获取属性的值
        String endTime = CommonMethod.getString(form,"endTime");  //Map 获取属性的值
        String level = CommonMethod.getString(form,"level");  //Map 获取属性的值
        String teacher = CommonMethod.getString(form,"teacher");  //Map 获取属性的值
        String description = CommonMethod.getString(form,"Edescription");  //Map 获取属性的值
        String position = CommonMethod.getString(form,"position");  //Map 获取属性的值
        EducationExperience a = null;
        Optional<EducationExperience> op;
        if(educationExperienceId != null) {
            op= educationExperienceRepository.findById(educationExperienceId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            educationExperienceId = getNewEducationExperienceId(); //获取EducationExperience新的主键
            a = new EducationExperience();
            a.setEducationExperienceId(educationExperienceId);
        }
        a.setName(name);
        a.setStartTime(startTime);
        a.setEndTime(endTime);
        a.setLevel(level);
        a.setProofTeacher(teacher);
        a.setDescription(description);
        a.setPosition(position);
        a.setTeacher(s);
        educationExperienceRepository.saveAndFlush(a);//插入新的EducationExperience记录
        return CommonMethod.getReturnData(a.getEducationExperienceId());  // 将educationExperienceId返回前端
    }
}

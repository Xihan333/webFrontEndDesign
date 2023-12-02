package org.fatmansoft.teach.controllers.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.NewProject;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.creativePratice.NewProjectRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.creativePractice.NewProjectService;
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
@RequestMapping("/api/newProject")
public class NewProjectController {
    @Autowired
    private NewProjectRepository newProjectRepository;
    @Autowired
    private NewProjectService newProjectService;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewNewProjectId(){
        Integer id = newProjectRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getNewProjectList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse geNewProjectList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = newProjectService.getNewProjectMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getStudentNewProject")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentNewProject(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = newProjectService.getNewProjectMapListByStudentId(studentId);
        System.out.println(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeacherNewProject")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherNewProject(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = newProjectService.getNewProjectMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/newProjectDelete")
    public DataResponse newProjectDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        NewProject a= null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            newProjectRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/show/newProjectWaiting")
    public DataResponse newProjectWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = newProjectService.getWaitingNewProjectMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/newProjectPassed")
    public DataResponse newProjectPassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = newProjectService.getPassedNewProjectMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/newProjectFailed")
    public DataResponse newProjectFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = newProjectService.getFailedNewProjectMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/newProjectPass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse newProjectPass(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        NewProject a= null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(1);
                newProjectRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/newProjectFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse newProjectFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        NewProject a= null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(2);
                newProjectRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getNewProjectInfo 前端点击项目信息列表时前端获取详细信息请求服务
     * @param dataRequest 从前端获取 newProjectId 查询项目信息的主键 newProject_id
     * @return  根据newProjectId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getNewProjectInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getNewProjectInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        NewProject a= null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(newProjectService.getMapFromNewProject(a)); //这里回传包含项目信息的Map对象
    }

    /**
     * NewProjectEditSave 前端项目信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * newProjectId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 newProject_id 返回前端
     */

    @PostMapping("/newProjectEditSave")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String isContribute = CommonMethod.getString(form,"isContribute");  //Map 获取属性的值
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String member = CommonMethod.getString(form,"member");  //Map 获取属性的值
        String don = CommonMethod.getString(form,"don");  //Map 获取属性的值
        String projectName = CommonMethod.getString(form,"projectName");  //Map 获取属性的值
        String exposition = CommonMethod.getString(form,"exposition");  //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Student student = null;
        Optional<Teacher> t = teacherRepository.findByPersonNum(num);
        Teacher teacher = null;
        if(s.isPresent()){
            student = s.get();
        }else if(t.isPresent()){
            teacher = t.get();
        }else{
            return CommonMethod.getReturnMessageError("该学生或教师不存在，请输入正确姓名或学号");
        }
        NewProject a = null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            newProjectId = getNewNewProjectId(); //获取NewProject新的主键
            a = new NewProject();
            a.setNewProjectId(newProjectId);
        }
        a.setIsContribute(isContribute);
        a.setGroupName(groupName);
        a.setMember(member);
        a.setDon(don);
        a.setProjectName(projectName);
        a.setExposition(exposition);
        a.setAuditStatus(1);
        if(teacher == null){
            a.setStudent(student);
        }else{
            a.setTeacher(teacher);
        }
        newProjectRepository.saveAndFlush(a);//插入新的newProject记录
        return CommonMethod.getReturnData(a.getNewProjectId());  // 将newProjectId返回前端
    }
    @PostMapping("/newProjectStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse newProjectStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String isContribute = CommonMethod.getString(form,"isContribute");  //Map 获取属性的值
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String member = CommonMethod.getString(form,"member");  //Map 获取属性的值
        String don = CommonMethod.getString(form,"don");  //Map 获取属性的值
        String projectName = CommonMethod.getString(form,"projectName");  //Map 获取属性的值
        String exposition = CommonMethod.getString(form,"exposition");  //Map 获取属性的值
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
        NewProject a = null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            newProjectId = getNewNewProjectId(); //获取NewProject新的主键
            a = new NewProject();
            a.setNewProjectId(newProjectId);
        }
        a.setIsContribute(isContribute);
        a.setGroupName(groupName);
        a.setMember(member);
        a.setDon(don);
        a.setProjectName(projectName);
        a.setExposition(exposition);
        a.setStudent(s);
        a.setAuditStatus(0);
        newProjectRepository.saveAndFlush(a);//插入新的NewProject记录
        return CommonMethod.getReturnData(a.getNewProjectId());  // 将newProjectId返回前端
    }

    @PostMapping("/newProjectTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse newProjectTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer newProjectId = dataRequest.getInteger("newProjectId");  //获取newProject_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String isContribute = CommonMethod.getString(form,"isContribute");  //Map 获取属性的值
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String member = CommonMethod.getString(form,"member");  //Map 获取属性的值
        String don = CommonMethod.getString(form,"don");  //Map 获取属性的值
        String projectName = CommonMethod.getString(form,"projectName");  //Map 获取属性的值
        String exposition = CommonMethod.getString(form,"exposition");  //Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        NewProject a = null;
        Optional<NewProject> op;
        if(newProjectId != null) {
            op= newProjectRepository.findById(newProjectId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            newProjectId = getNewNewProjectId(); //获取NewProject新的主键
            a = new NewProject();
            a.setNewProjectId(newProjectId);
        }
        a.setIsContribute(isContribute);
        a.setGroupName(groupName);
        a.setMember(member);
        a.setDon(don);
        a.setProjectName(projectName);
        a.setExposition(exposition);
        a.setTeacher(s);
        a.setAuditStatus(0);
        newProjectRepository.saveAndFlush(a);//插入新的NewProject记录
        return CommonMethod.getReturnData(a.getNewProjectId());  // 将newProjectId返回前端
    }
}

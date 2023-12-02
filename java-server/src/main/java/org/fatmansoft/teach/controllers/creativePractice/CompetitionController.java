package org.fatmansoft.teach.controllers.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.Competition;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.creativePratice.CompetitionRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.creativePractice.CompetitionService;
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
@RequestMapping("/api/competition")
public class CompetitionController {
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewCompetitionId(){
        Integer id = competitionRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getCompetitionList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = competitionService.getCompetitionMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getStudentCompetition")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentCompetition(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = competitionService.getCompetitionMapListByStudentId(studentId);
        System.out.println(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeacherCompetition")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherCompetition(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = competitionService.getCompetitionMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/competitionDelete")
    public DataResponse competitionDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Competition a= null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            competitionRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getCompetitionInfo 前端点击竞赛信息列表时前端获取详细信息请求服务
     * @param dataRequest 从前端获取 competitionId 查询竞赛信息的主键 competition_id
     * @return  根据competitionId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getCompetitionInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCompetitionInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Competition a= null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(competitionService.getMapFromCompetition(a)); //这里回传包含竞赛信息的Map对象
    }

    /**
     * CompetitionEditSave 前端竞赛信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * competitionId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 competition_id 返回前端
     */

    @PostMapping("/competitionEditSave")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String title = CommonMethod.getString(form,"competitionTitle");
        String competitionLevel = CommonMethod.getString(form,"competitionLevel");
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String member = CommonMethod.getString(form,"member");  //Map 获取属性的值
        String don = CommonMethod.getString(form,"don");  //Map 获取属性的值
        String awardStatus = CommonMethod.getString(form,"awardStatus");  //Map 获取属性的值
        String rank = CommonMethod.getString(form,"rank");  //Map 获取属性的值
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
        Competition a = null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            competitionId = getNewCompetitionId(); //获取Competition新的主键
            a = new Competition();
            a.setCompetitionId(competitionId);
        }
        a.setTitle(title);
        a.setCompetitionLevel(competitionLevel);
        a.setGroupName(groupName);
        a.setMember(member);
        a.setDon(don);
        a.setAwardStatus(awardStatus);
        a.setRank(rank);
        a.setAuditStatus(1);
        if(teacher == null){
            a.setStudent(student);
        }else{
            a.setTeacher(teacher);
        }
        competitionRepository.saveAndFlush(a);//插入新的competition记录
        return CommonMethod.getReturnData(a.getCompetitionId());  // 将competitionId返回前端
    }

    @PostMapping("/competitionStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse competitionStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String title = CommonMethod.getString(form,"competitionTitle");
        String competitionLevel = CommonMethod.getString(form,"competitionLevel");
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String member = CommonMethod.getString(form,"member");  //Map 获取属性的值
        String don = CommonMethod.getString(form,"don");  //Map 获取属性的值
        String awardStatus = CommonMethod.getString(form,"awardStatus");  //Map 获取属性的值
        String rank = CommonMethod.getString(form,"rank");  //Map 获取属性的值
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
        Competition a = null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            competitionId = getNewCompetitionId(); //获取Competition新的主键
            a = new Competition();
            a.setCompetitionId(competitionId);
        }
        a.setTitle(title);
        a.setCompetitionLevel(competitionLevel);
        a.setGroupName(groupName);
        a.setMember(member);
        a.setDon(don);
        a.setAwardStatus(awardStatus);
        a.setRank(rank);
        a.setStudent(s);
        a.setAuditStatus(0);
        competitionRepository.saveAndFlush(a);//插入新的Competition记录
        return CommonMethod.getReturnData(a.getCompetitionId());  // 将competitionId返回前端
    }

    @PostMapping("/competitionTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse competitionTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String title = CommonMethod.getString(form,"competitionTitle");
        String competitionLevel = CommonMethod.getString(form,"competitionLevel");
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String member = CommonMethod.getString(form,"member");  //Map 获取属性的值
        String don = CommonMethod.getString(form,"don");  //Map 获取属性的值
        String awardStatus = CommonMethod.getString(form,"awardStatus");  //Map 获取属性的值
        String rank = CommonMethod.getString(form,"rank");  //Map 获取属性的值
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
        Competition a = null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            competitionId = getNewCompetitionId(); //获取Competition新的主键
            a = new Competition();
            a.setCompetitionId(competitionId);
        }
        a.setTitle(title);
        a.setCompetitionLevel(competitionLevel);
        a.setGroupName(groupName);
        a.setMember(member);
        a.setDon(don);
        a.setAwardStatus(awardStatus);
        a.setRank(rank);
        a.setTeacher(s);
        a.setAuditStatus(0);
        competitionRepository.saveAndFlush(a);//插入新的Competition记录
        return CommonMethod.getReturnData(a.getCompetitionId());  // 将competitionId返回前端
    }

    @PostMapping("/show/competitionWaiting")
    public DataResponse competitionWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = competitionService.getWaitingCompetitionMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/competitionPassed")
    public DataResponse competitionPassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = competitionService.getPassedCompetitionMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/competitionFailed")
    public DataResponse competitionFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = competitionService.getFailedCompetitionMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/competitionPass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse competitionPass(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Competition a= null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(1);
                competitionRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/competitionFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse competitionFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");  //获取competition_id值
        Competition a= null;
        Optional<Competition> op;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(2);
                competitionRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


}

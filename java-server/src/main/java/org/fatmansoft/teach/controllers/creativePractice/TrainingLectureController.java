package org.fatmansoft.teach.controllers.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.TrainingLecture;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.creativePratice.TrainingLectureRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.creativePractice.TrainingLectureService;
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
@RequestMapping("/api/trainingLecture")
public class TrainingLectureController {
    @Autowired
    private TrainingLectureRepository trainingLectureRepository;
    @Autowired
    private TrainingLectureService trainingLectureService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewTrainingLectureId(){
        Integer id = trainingLectureRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getTrainingLectureList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse geTrainingLectureList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = trainingLectureService.getTrainingLectureMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getStudentTrainingLecture")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentTrainingLecture(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = trainingLectureService.getTrainingLectureMapListByStudentId(studentId);
        System.out.println(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeacherTrainingLecture")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherTrainingLecture(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = trainingLectureService.getTrainingLectureMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/trainingLectureDelete")
    public DataResponse trainingLectureDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        TrainingLecture a= null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            trainingLectureRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getTrainingLectureInfo 前端点击竞赛信息列表时前端获取详细信息请求服务
     * @param dataRequest 从前端获取 trainingLectureId 查询竞赛信息的主键 trainingLecture_id
     * @return  根据trainingLectureId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getTrainingLectureInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTrainingLectureInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        TrainingLecture a= null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(trainingLectureService.getMapFromTrainingLecture(a)); //这里回传包含竞赛信息的Map对象
    }

    /**
     * TrainingLectureEditSave 前端竞赛信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * trainingLectureId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 trainingLecture_id 返回前端
     */

    @PostMapping("/trainingLectureEditSave")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String date = CommonMethod.getString(form,"date");
        String location = CommonMethod.getString(form,"location");
        String speaker = CommonMethod.getString(form,"speaker");  //Map 获取属性的值
        String theme = CommonMethod.getString(form,"theme");  //Map 获取属性的值
        Integer extendFraction = CommonMethod.getInteger(form,"extendFraction");  //Map 获取属性的值
        String inspiration = CommonMethod.getString(form,"inspiration");  //Map 获取属性的值
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
        TrainingLecture a = null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            trainingLectureId = getNewTrainingLectureId(); //获取TrainingLecture新的主键
            a = new TrainingLecture();
            a.setTrainingLectureId(trainingLectureId);
        }
        a.setDate(date);
        a.setLocation(location);
        a.setSpeaker(speaker);
        a.setTheme(theme);
        a.setExtendFraction(extendFraction);
        a.setInspiration(inspiration);
        a.setAuditStatus(1);
        if(teacher == null){
            a.setStudent(student);
        }else{
            a.setTeacher(teacher);
        }
        trainingLectureRepository.saveAndFlush(a);//插入新的trainingLecture记录
        return CommonMethod.getReturnData(a.getTrainingLectureId());  // 将trainingLectureId返回前端
    }

    @PostMapping("/trainingLectureStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse trainingLectureStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String date = CommonMethod.getString(form,"date");
        String location = CommonMethod.getString(form,"location");
        String speaker = CommonMethod.getString(form,"speaker");  //Map 获取属性的值
        String theme = CommonMethod.getString(form,"theme");  //Map 获取属性的值
        Integer extendFraction = CommonMethod.getInteger(form,"extendFraction");   //Map 获取属性的值
        String inspiration = CommonMethod.getString(form,"inspiration");  //Map 获取属性的值
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
        TrainingLecture a = null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            trainingLectureId = getNewTrainingLectureId(); //获取TrainingLecture新的主键
            a = new TrainingLecture();
            a.setTrainingLectureId(trainingLectureId);
        }
        a.setDate(date);
        a.setLocation(location);
        a.setSpeaker(speaker);
        a.setTheme(theme);
        a.setExtendFraction(extendFraction);
        a.setInspiration(inspiration);
        a.setStudent(s);
        a.setAuditStatus(0);
        trainingLectureRepository.saveAndFlush(a);//插入新的TrainingLecture记录
        return CommonMethod.getReturnData(a.getTrainingLectureId());  // 将trainingLectureId返回前端
    }

    @PostMapping("/trainingLectureTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse trainingLectureTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String date = CommonMethod.getString(form,"date");
        String location = CommonMethod.getString(form,"location");
        String speaker = CommonMethod.getString(form,"speaker");  //Map 获取属性的值
        String theme = CommonMethod.getString(form,"theme");  //Map 获取属性的值
        Integer extendFraction = CommonMethod.getInteger(form,"extendFraction"); //Map 获取属性的值
        String inspiration = CommonMethod.getString(form,"inspiration");  //Map 获取属性的值
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
        TrainingLecture a = null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            trainingLectureId = getNewTrainingLectureId(); //获取TrainingLecture新的主键
            a = new TrainingLecture();
            a.setTrainingLectureId(trainingLectureId);
        }
        a.setDate(date);
        a.setLocation(location);
        a.setSpeaker(speaker);
        a.setTheme(theme);
        a.setExtendFraction(extendFraction);
        a.setInspiration(inspiration);
        a.setTeacher(s);
        a.setAuditStatus(0);
        trainingLectureRepository.saveAndFlush(a);//插入新的TrainingLecture记录
        return CommonMethod.getReturnData(a.getTrainingLectureId());  // 将trainingLectureId返回前端
    }

    @PostMapping("/show/trainingLectureWaiting")
    public DataResponse trainingLectureWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = trainingLectureService.getWaitingTrainingLectureMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/trainingLecturePassed")
    public DataResponse trainingLecturePassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = trainingLectureService.getPassedTrainingLectureMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/trainingLectureFailed")
    public DataResponse trainingLectureFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = trainingLectureService.getFailedTrainingLectureMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/trainingLecturePass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse trainingLecturePass(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        TrainingLecture a= null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(1);
                trainingLectureRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/trainingLectureFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse trainingLectureFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer trainingLectureId = dataRequest.getInteger("trainingLectureId");  //获取trainingLecture_id值
        TrainingLecture a= null;
        Optional<TrainingLecture> op;
        if(trainingLectureId != null) {
            op= trainingLectureRepository.findById(trainingLectureId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(2);
                trainingLectureRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

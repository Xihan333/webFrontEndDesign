package org.fatmansoft.teach.controllers.teacher;

import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.ScientificPayoffs;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.repository.teacher.ScientificPayoffsRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.teacher.ScientificPayoffsService;
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
@RequestMapping("/api/scientificPayoffs")
public class ScientificPayoffsController {
    @Autowired
    private ScientificPayoffsRepository scientificPayoffsRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScientificPayoffsService scientificPayoffsService;

    public synchronized  Integer getNewScientificPayoffsId(){
        Integer id = scientificPayoffsRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getScientificPayoffsList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse geScientificPayoffsList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = scientificPayoffsService.getScientificPayoffsMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeacherScientificPayoffs")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherScientificPayoffs(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Integer teacherId = s.getTeacherId();
        List dataList = scientificPayoffsService.getScientificPayoffsMapListByTeacherId(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/scientificPayoffsDelete")
    public DataResponse scientificPayoffsDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer scientificPayoffsId = dataRequest.getInteger("scientificPayoffsId");  //获取scientificPayoffs_id值
        System.out.println(scientificPayoffsId);
        ScientificPayoffs a= null;
        Optional<ScientificPayoffs> op;
        if(scientificPayoffsId != null) {
            op= scientificPayoffsRepository.findById(scientificPayoffsId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            scientificPayoffsRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getScientificPayoffsInfo 前端点击竞赛信息列表时前端获取详细信息请求服务
     * @param dataRequest 从前端获取 scientificPayoffsId 查询竞赛信息的主键 scientificPayoffs_id
     * @return  根据scientificPayoffsId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getScientificPayoffsInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getScientificPayoffsInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer scientificPayoffsId = dataRequest.getInteger("scientificPayoffsId");  //获取scientificPayoffs_id值
        ScientificPayoffs a= null;
        Optional<ScientificPayoffs> op;
        if(scientificPayoffsId != null) {
            op= scientificPayoffsRepository.findById(scientificPayoffsId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(scientificPayoffsService.getMapFromScientificPayoffs(a)); //这里回传包含竞赛信息的Map对象
    }


    @PostMapping("/scientificPayoffsTeacherEditSave")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse scientificPayoffsTeacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer scientificPayoffsId = dataRequest.getInteger("scientificPayoffsId");  //获取scientificPayoffs_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");
        String authors = CommonMethod.getString(form,"authors");
        String paperName = CommonMethod.getString(form,"paperName");  //Map 获取属性的值

        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        ScientificPayoffs a = null;
        Optional<ScientificPayoffs> op;
        if(scientificPayoffsId != null) {
            op= scientificPayoffsRepository.findById(scientificPayoffsId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            scientificPayoffsId = getNewScientificPayoffsId(); //获取ScientificPayoffs新的主键
            a = new ScientificPayoffs();
            a.setScientificPayoffsId(scientificPayoffsId);
        }
        a.setDay(day);
        a.setAuthors(authors);
        a.setPaperName(paperName);
        a.setTeacher(s);
        scientificPayoffsRepository.saveAndFlush(a);//插入新的ScientificPayoffs记录
        return CommonMethod.getReturnData(a.getScientificPayoffsId());  // 将scientificPayoffsId返回前端
    }
}

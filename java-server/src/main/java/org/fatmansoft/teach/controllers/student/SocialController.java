package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Social;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.student.SocialRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.student.StudentService;
import org.fatmansoft.teach.service.student.SocialService;
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
@RequestMapping("/api/social")
public class SocialController {
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private StudentService studentService;
    @Autowired
    private SocialRepository socialRepository;
    @Autowired
    private SocialService socialService;

    public synchronized  Integer getNewSocialId(){
        Integer id = socialRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getSocialList")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DataResponse geSocialList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = socialService.getSocialMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getStudentSocial")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentSocial(@Valid @RequestBody DataRequest dataRequest) {
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
        List dataList = socialService.getSocialMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/socialDelete")
    public DataResponse socialDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialId = dataRequest.getInteger("socialId");  //获取social_id值
        Social a= null;
        Optional<Social> op;
        if(socialId != null) {
            op= socialRepository.findById(socialId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            socialRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getSocialInfo 前端点击实习信息列表时前端获取详细信息请求服务
     * @param dataRequest 从前端获取 socialId 查询旅程信息的主键 social_id
     * @return  根据socialId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getSocialInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getSocialInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialId = dataRequest.getInteger("socialId");  //获取social_id值
        Social a= null;
        Optional<Social> op;
        if(socialId != null) {
            op= socialRepository.findById(socialId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(socialService.getMapFromSocial(a)); //这里回传包含聚会信息的Map对象
    }

    /**
     * SocialEditSave 前端实习信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * socialId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 social_id 返回前端
     */

    @PostMapping("/socialEditSave")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialId = dataRequest.getInteger("socialId");  //获取social_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String theme = CommonMethod.getString(form,"theme");//Map 获取属性的值
        String digest = CommonMethod.getString(form,"digest");
        String harvest = CommonMethod.getString(form,"harvest");
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Student student = null;
        if(s.isPresent()){
            student = s.get();
        }else{
            return CommonMethod.getReturnMessageError("该学生不存在，请输入正确姓名或学号");
        }
        Social a = null;
        Optional<Social> op;
        if(socialId != null) {
            op= socialRepository.findById(socialId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            socialId = getNewSocialId(); //获取Social新的主键
            a = new Social();
            a.setSocialId(socialId);
        }
        a.setDay(day);
        a.setGroupName(groupName);
        a.setTheme(theme);
        a.setDigest(digest);
        a.setHarvest(harvest);
        a.setAuditStatus(1);
        a.setStudent(student);
        socialRepository.saveAndFlush(a);//插入新的social记录
        return CommonMethod.getReturnData(a.getSocialId());  // 将socialId返回前端
    }
    @PostMapping("/socialStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse socialStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialId = dataRequest.getInteger("socialId");  //获取social_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String groupName = CommonMethod.getString(form,"groupName");  //Map 获取属性的值
        String theme = CommonMethod.getString(form,"theme");//Map 获取属性的值
        String digest = CommonMethod.getString(form,"digest");
        String harvest = CommonMethod.getString(form,"harvest");
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
        Social a = null;
        Optional<Social> op;
        if(socialId != null) {
            op= socialRepository.findById(socialId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            socialId = getNewSocialId(); //获取Social新的主键
            a = new Social();
            a.setSocialId(socialId);
        }
        a.setDay(day);
        a.setGroupName(groupName);
        a.setTheme(theme);
        a.setDigest(digest);
        a.setHarvest(harvest);
        a.setStudent(s);
        a.setAuditStatus(0);
        socialRepository.saveAndFlush(a);//插入新的Social记录
        return CommonMethod.getReturnData(a.getSocialId());  // 将socialId返回前端
    }

    @PostMapping("/show/socialWaiting")
    public DataResponse socialWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = socialService.getWaitingSocialMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/socialPassed")
    public DataResponse socialPassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = socialService.getPassedSocialMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/socialFailed")
    public DataResponse socialFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = socialService.getFailedSocialMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/socialPass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse socialPass(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialId = dataRequest.getInteger("socialId");  //获取social_id值
        Social a= null;
        Optional<Social> op;
        if(socialId != null) {
            op= socialRepository.findById(socialId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(1);
                socialRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/examine/socialFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse socialFail(@Valid @RequestBody DataRequest dataRequest) {
        Integer socialId = dataRequest.getInteger("socialId");  //获取social_id值
        Social a= null;
        Optional<Social> op;
        if(socialId != null) {
            op= socialRepository.findById(socialId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setAuditStatus(2);
                socialRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

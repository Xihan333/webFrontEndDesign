package org.fatmansoft.teach.controllers.dailyActivity;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Travel;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.dailyActivity.TravelRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.service.dailyActivity.TravelService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/travel")

public class TravelController {

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private StudentService studentService;
    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private TravelService travelService;

    public synchronized  Integer getNewTravelId(){
        Integer id = travelRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    @PostMapping("/getTravelList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTravelList(@Valid @RequestBody DataRequest dataRequest) {
        String dayLocation= dataRequest.getString("dayLocation");
        List dataList = travelService.getTravelMapList(dayLocation);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getByStudentId")
    public DataResponse getByStudentId(@Valid @RequestBody DataRequest dataRequest){
        Integer studentId = dataRequest.getInteger("studentId");
        List<Travel> dataList = travelRepository.findTravelByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getStudentTravel")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentTravel(@Valid @RequestBody DataRequest dataRequest){
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
        List dataList = travelService.getTravelMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/travelDelete")
    public DataResponse travelDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer travelId = dataRequest.getInteger("travelId");  //获取travel_id值
        Travel a= null;
        Optional<Travel> op;
        if(travelId != null) {
            op= travelRepository.findById(travelId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            travelRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    /**
     * getTravelInfo 前端点击旅程列表时前端获取旅程详细信息请求服务
     * @param dataRequest 从前端获取 travelId 查询旅程信息的主键 travel_id
     * @return  根据travelId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getTravelInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTravelInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer travelId = dataRequest.getInteger("travelId");  //获取travel_id值
        Travel a= null;
        Optional<Travel> op;
        if(travelId != null) {
            op= travelRepository.findById(travelId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(travelService.getMapFromTravel(a)); //这里回传包含旅程信息的Map对象
    }

    /**
     * TravelEditSave 前端旅程信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * travelId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改旅程的主键 travel_id 返回前端
     */

    @PostMapping("/travelEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse travelEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer travelId = dataRequest.getInteger("travelId");  //获取punishment_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String title = CommonMethod.getString(form,"title");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");//Map 获取属性的值
        String introduction = CommonMethod.getString(form,"introduction");//Map 获取属性的值
        String num = CommonMethod.getString(form,"num");
        String name = CommonMethod.getString(form,"name");
        Optional<Student> s = studentRepository.findByPersonNum(num);
        Optional<Student> s1 = studentRepository.findByPersonName(name);
        Student student = null;
        if(s.isPresent()){
            student = s.get();
        }else if (s1.isPresent()){
            student = s1.get();
        }else {
            return CommonMethod.getReturnMessageError("该学生不存在，请输入正确的学号或姓名！");
        }
        Travel a = null;
        Optional<Travel> op;
        if(travelId != null) {
            op= travelRepository.findById(travelId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            travelId = getNewTravelId(); //获取Punishment新的主键
            a = new Travel();
            a.setTravelId(travelId);
        }
        a.setDay(day);
        a.setTitle(title);
        a.setLocation(location);
        a.setIntroduction(introduction);
        a.setStudent(student);
        travelRepository.saveAndFlush(a);//插入新的travel记录
        return CommonMethod.getReturnData(a.getTravelId());  // 将travelId返回前端
    }
    @PostMapping("/travelStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse travelStudentEditSave(@Valid @RequestBody DataRequest dataRequest){
        Integer travelId = dataRequest.getInteger("travelId");  //获取punishment_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String day = CommonMethod.getString(form,"day");  //Map 获取属性的值
        String title = CommonMethod.getString(form,"title");  //Map 获取属性的值
        String location = CommonMethod.getString(form,"location");//Map 获取属性的值
        String introduction = CommonMethod.getString(form,"introduction");//Map 获取属性的值
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
        Travel a = null;
        Optional<Travel> op;
        if(travelId != null) {
            op= travelRepository.findById(travelId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            travelId = getNewTravelId(); //获取Punishment新的主键
            a = new Travel();
            a.setTravelId(travelId);
        }
        a.setDay(day);
        a.setTitle(title);
        a.setLocation(location);
        a.setIntroduction(introduction);
        a.setStudent(s);
        travelRepository.saveAndFlush(a);//插入新的travel记录
        return CommonMethod.getReturnData(a.getTravelId());
    }

}

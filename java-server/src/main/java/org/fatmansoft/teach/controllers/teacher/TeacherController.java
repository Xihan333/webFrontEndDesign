package org.fatmansoft.teach.controllers.teacher;



import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.system.EUserType;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.ScoreRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.repository.teacher.ScientificPayoffsRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.student.CourseService;
import org.fatmansoft.teach.service.system.BaseService;
import org.fatmansoft.teach.service.student.AchievementService;
import org.fatmansoft.teach.service.teacher.ScientificPayoffsService;
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherIntroduceService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.FormatJudge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")

public class TeacherController {

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private TeacherRepository teacherRepository;  //教师数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //教师数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherIntroduceService teacherIntroduceService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private ScientificPayoffsRepository scientificPayoffsRepository;

    @Autowired
    private ScientificPayoffsService scientificPayoffsService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;


    public synchronized Integer getNewPersonId(){  //synchronized 同步方法
        Integer  id = personRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };


    public synchronized Integer getNewUserId(){
        Integer  id = userRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    public synchronized Integer getNewTeacherId(){
        Integer  id = teacherRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };



    @PostMapping("/getTeacherItemOptionList")
    public OptionItemList getTeacherItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Teacher> sList = teacherRepository.findTeacherListByNumName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Teacher s : sList) {
            itemList.add(new OptionItem(s.getTeacherId(), s.getPerson().getNum(), s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = teacherService.getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }



    @PostMapping("/teacherDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");  //获取teacher_id值
        Teacher s= null;
        Optional<Teacher> op;
        if(teacherId != null) {
            op= teacherRepository.findById(teacherId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该教师的账户
            if(uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该教师的账户
            }
            Person p = s.getPerson();
            teacherRepository.delete(s);    //首先数据库永久删除教师信息
            personRepository.delete(p);   // 然后数据库永久删除教师信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }



    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s= null;
        Optional<Teacher> op;
        if(teacherId != null) {
            op= teacherRepository.findById(teacherId); //根据教师主键从数据库查询教师的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(teacherService.getMapFromTeacher(s)); //这里回传包含教师信息的Map对象
    }


    @PostMapping("/teacherEdit")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse teacherEdit(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherEdit(dataRequest);
    }

    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        String card = CommonMethod.getString(form,"card");
        if(!(card.equals("")||FormatJudge.IdCard(card))){
           return CommonMethod.getReturnMessageError("身份证号不合法，请重新输入！");
        }
        String birthday = CommonMethod.getString(form,"birthday");
        if((!birthday.equals(""))&&(!card.equals(""))&&(!FormatJudge.birthday(birthday,card))){
            return CommonMethod.getReturnMessageError("您选择的生日与身份证号不符，请重新选择！");
        }
        Teacher s= null;
        Person p;
        User u;
        Optional<Teacher> op;
        Integer personId;
        if(teacherId != null) {
            op= teacherRepository.findById(teacherId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if(nOp.isPresent()) {
            if(s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if(s == null) {
            personId = getNewPersonId(); //获取Person新的主键，这个是线程同步问题;
            p = new Person();
            p.setPersonId(personId);
            p.setNum(num);
            p.setType("2");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            String password = encoder.encode("123456");
            u= new User();
            u.setUserId(getNewUserId());
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER));
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Teacher();   // 创建实体对象
            s.setTeacherId(getNewTeacherId());
            s.setPerson(p);
            teacherRepository.saveAndFlush(s);  //插入新的Teacher记录
        }else {
            p = s.getPerson();
            personId = p.getPersonId();
        }
        if(!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User>uOp = userRepository.findByPersonPersonId(personId);
            if(uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form,"name"));
        p.setDept(CommonMethod.getString(form,"dept"));
        p.setCard(card);
        p.setGender(CommonMethod.getString(form,"gender"));
        p.setBirthday(birthday);
        p.setGender(CommonMethod.getString(form,"gender"));
        p.setEmail(CommonMethod.getString(form,"email"));
        p.setPhone(CommonMethod.getString(form,"phone"));
        p.setAddress(CommonMethod.getString(form,"address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setTitle(CommonMethod.getString(form,"title"));
        s.setDegree(CommonMethod.getString(form,"degree"));
        s.setDirection(CommonMethod.getString(form,"direction"));
        teacherRepository.save(s);  //修改保存教师信息
        return CommonMethod.getReturnData(s.getTeacherId());  // 将teacherId返回前端
    }



    /**
     * getTeacherIntroduceData 前端获取教师个人简历数据请求服务
     * @param dataRequest 从前端获取 teacherId 查询教师信息的主键 teacher_id
     * @return  根据teacherId从数据库中查出相关数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getTeacherIntroduceData")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse getTeacherIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Teacher对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Integer teacherId = s.getTeacherId();
        Map info = teacherService.getMapFromTeacher(s);  // 查询教师信息Map对象
        info.put("introduce", teacherIntroduceService.getIntroduceDataMap(u.getUserId()));
        Map data = new HashMap();
        data.put("info",info);
        data.put("achievementList",achievementService.getPassedAchievementMapList(s.getPerson().getNum()));
        data.put("scientificPayoffsList",scientificPayoffsService.getScientificPayoffsMapList(s.getPerson().getNum()));
        List<TeacherCourse> tcList = teacherCourseRepository.findCourseListByTeacherId(teacherId);  //数据库查询操作
        data.put("coursesList",teacherCourseService.TeacherCourseList(tcList));
        return CommonMethod.getReturnData(data);//将前端所需数据保留Map对象里，返还前端
    }

    /**
     * saveTeacherIntroduce 前端教师个人简介信息introduce提交服务
     * @param dataRequest 从前端获取 teacherId teacher表 teacher_id introduce 教师个人简介信息
     * @return  操作正常
     */

    @PostMapping("/saveTeacherIntroduce")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DataResponse saveTeacherIntroduce(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        String introduce = dataRequest.getString("introduce");
        Optional<Teacher> sOp= teacherRepository.findById(teacherId);
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Person p = s.getPerson();
        p.setIntroduce(introduce);
        personRepository.save(p);
        return CommonMethod.getReturnMessageOK();
    }



    /**
     * getTeacherListExcl 前端下载导出教师基本信息Excl表数据
     * @param dataRequest
     * @return
     */
    @PostMapping("/getTeacherListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List list = teacherService.getTeacherMapList(numName);
        Integer widths[] = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String titles[] = {"序号","学号", "姓名", "学院", "职称", "学位", "证件号码", "性别","出生日期","邮箱","电话","地址"};
        String outPutSheetName = "teacher.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for(j=0;j < widths.length;j++) {
            sheet.setColumnWidth(j,widths[j]*256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell cell[] = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map m;
        if (list != null && list.size() > 0) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = (Map) list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m,"num"));
                cell[2].setCellValue(CommonMethod.getString(m,"name"));
                cell[3].setCellValue(CommonMethod.getString(m,"dept"));
                cell[4].setCellValue(CommonMethod.getString(m,"title"));
                cell[5].setCellValue(CommonMethod.getString(m,"degree"));
                cell[6].setCellValue(CommonMethod.getString(m,"card"));
                cell[7].setCellValue(CommonMethod.getString(m,"genderName"));
                cell[8].setCellValue(CommonMethod.getString(m,"birthday"));
                cell[9].setCellValue(CommonMethod.getString(m,"email"));
                cell[10].setCellValue(CommonMethod.getString(m,"phone"));
                cell[11].setCellValue(CommonMethod.getString(m,"address"));
            }
        }
        try {
            StreamingResponseBody stream = outputStream -> {
                wb.write(outputStream);
            };
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    /**
     * getTeacherIntroducePdf 生成获取个人简历的PDF数据流服务
     * @param dataRequest  teacherId 教师主键
     * @return  返回PDF文件二进制数据
     */
    @PostMapping("/getTeacherIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getTeacherIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = teacherRepository.getById(teacherId);  //查询获得Teacher对象
        Map info = teacherService.getMapFromTeacher(s); //获得教师信息
        String content = (String)info.get("introduce");  // 个人简历的HTML字符串
        content = CommonMethod.addHeadInfo(content,"<style> html { font-family: \"SourceHanSansSC\", \"Open Sans\";}  </style> <meta charset='UTF-8' />  <title>Insert title here</title>");  // 插入由HTML转换PDF需要的头信息
        System.out.println(content);
        content = CommonMethod.removeErrorString(content,"&nbsp;","style=\"font-family: &quot;&quot;;\""); //删除无法转化不合法的HTML标签
        content= CommonMethod.replaceNameValue(content,info); //将HTML中标记串${name}等替换成教师实际的信息
        return baseService.getPdfDataFromHtml(content); //生成教师简历PDF二进制数据
    }

    @GetMapping("/getMyInfo")
    public DataResponse getMyInfo() {
        return teacherService.getMyInfo();
    }

}

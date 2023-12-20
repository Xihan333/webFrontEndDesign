package org.fatmansoft.teach.controllers.student;

import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.student.Clazz;
import org.fatmansoft.teach.models.student.Grade;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.student.Score;
import org.fatmansoft.teach.models.system.EUserType;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.student.*;
import org.fatmansoft.teach.service.system.BaseService;
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
/**
 *  StudentController 主要是为学生管理数据管理提供的Web请求服务
 *
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student")

public class StudentController {

    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用

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
    private AchievementService achievementService;

    @Autowired
    private StudentIntroduceService studentIntroduceService;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入

    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入

    @Autowired
    private StudentService studentService;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private ClazzService clazzService;


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

    /**
     *  获取 student 表的新的Id StringBoot 对SqLite 主键自增支持不好  插入记录是需要设置主键ID，编写方法获取新的 student_id
     * @return
     */
    public synchronized Integer getNewStudentId(){
        Integer  id = studentRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    //根据学号及姓名查询学生 numName
    @PostMapping("/getStudentOptionItemList")
    public DataResponse getStudentOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getStudentOptionItemList(dataRequest);
    }


    //根据clazzId获取班级学生 （可在互评中使用）
    @PostMapping("/getStudentOptionItemListByClazzId")
    public DataResponse getCourseOptionItemListByClazzId(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getCourseOptionItemListByClazzId(dataRequest);
    }

    //获取当前用所在班级学生名单（不包括该学生本人）
    @PostMapping("/getMyClassStudents")
    public DataResponse getMyClassStudents() {
        return studentService.getMyClassStudents();
    }

    //根据courseId获取选课学生
    @PostMapping("/getStudentOptionItemListByCourseId")
    public DataResponse getCourseOptionItemListByCourseId(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getStudentOptionItemListByCourseId(dataRequest);
    }


    /**
     *  getStudentList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getStudentList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = studentService.getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     * @param dataRequest  前端studentId 要删除的学生的主键 student_id
     * @return  正常操作
     */
    @PostMapping("/studentDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");  //获取student_id值
        Student s= null;
        Optional<Student> op;
        if(studentId != null) {
            op= studentRepository.findById(studentId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该学生的账户
            if(uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该学生的账户
            }
            Person p = s.getPerson();
            studentRepository.delete(s);    //首先数据库永久删除学生信息
            personRepository.delete(p);   // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return  根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getStudentInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Student s= null;
        Optional<Student> op;
        if(studentId != null) {
            op= studentRepository.findById(studentId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(studentService.getMapFromStudent(s)); //这里回传包含学生信息的Map对象
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/studentEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.studentEditSave(dataRequest);
    }


    @PostMapping("/studentEdit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse studentEdit(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.studentEdit(dataRequest);
    }


    /**
     * getStudentIntroduceData 前端获取学生个人简历数据请求服务
     */
    @GetMapping("/getStudentIntroduceData")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentIntroduceData() {
        return studentService.getStudentIntroduceData();
    }

    /**
     * saveStudentIntroduce 前端学生个人简介信息introduce提交服务
     * @param dataRequest 从前端获取 studentId student表 student_id introduce 学生个人简介信息
     * @return  操作正常
     */

    @PostMapping("/saveStudentIntroduce")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse saveStudentIntroduce(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String introduce = dataRequest.getString("introduce");
        Optional<Student> sOp= studentRepository.findById(studentId);
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Person p = s.getPerson();
        p.setIntroduce(introduce);
        personRepository.save(p);
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 未使用（复用需修改）
     * getStudentListExcl 前端下载导出学生基本信息Excl表数据
     * @param dataRequest
     * @return
     */
    @PostMapping("/getStudentListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getStudentListExcl(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List list = studentService.getStudentMapList(numName);
        Integer widths[] = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String titles[] = {"序号","学号", "姓名", "学院", "专业", "班级", "证件号码", "性别","出生日期","邮箱","电话","地址"};
        String outPutSheetName = "student.xlsx";
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
                cell[4].setCellValue(CommonMethod.getString(m,"major"));
                cell[5].setCellValue(CommonMethod.getString(m,"className"));
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
     * getStudentIntroducePdf 生成获取个人简历的PDF数据流服务
     * @param dataRequest  studentId 学生主键
     * @return  返回PDF文件二进制数据
     */
    @PostMapping("/getStudentIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getStudentIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Student s = studentRepository.getById(studentId);  //查询获得Student对象
        Map info = studentService.getMapFromStudent(s); //获得学生信息
        String content = (String)info.get("introduce");  // 个人简历的HTML字符串
        content = CommonMethod.addHeadInfo(content,"<style> html { font-family: \"SourceHanSansSC\", \"Open Sans\";}  </style> <meta charset='UTF-8' />  <title>Insert title here</title>");  // 插入由HTML转换PDF需要的头信息
        System.out.println(content);
        content = CommonMethod.removeErrorString(content,"&nbsp;","style=\"font-family: &quot;&quot;;\""); //删除无法转化不合法的HTML标签
        content= CommonMethod.replaceNameValue(content,info); //将HTML中标记串${name}等替换成学生实际的信息
        return baseService.getPdfDataFromHtml(content); //生成学生简历PDF二进制数据
    }


}

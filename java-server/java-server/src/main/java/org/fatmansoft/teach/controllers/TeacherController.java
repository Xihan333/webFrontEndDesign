package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class TeacherController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用TeacherRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， teacherRepository 相当于TeacherRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Value("${attach.folder}")
    private String attachFolder;

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    //getTeacherMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("teacherNum",s.getTeacherNum())， teacherNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getTeacherMapList(String numName) {
        List dataList = new ArrayList();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Teacher s;
        Map m;
        String courseParas,teacherNameParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("teacherNum",s.getTeacherNum());
            m.put("teacherName",s.getTeacherName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("researchDirection", s.getResearchDirection());
            m.put("age",s.getAge());
            m.put("dept",s.getDept());
            m.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("EMail", s.getEMail());
            m.put("telephone", s.getTelephone());
            m.put("selfAssessment", s.getSelfAssessment());
            String image0  = getPersonImageString(attachFolder,s.getTeacherNum(), "0");
            m.put("image0", image0);

            dataList.add(m);
        }
        return dataList;
    }
    //teacher页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getTeacherMapList，返回所有学生数据，
    @PostMapping("/teacherInit")
    @PreAuthorize("hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse teacherInit(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        if (username == null) username = "";
        List dataList = getTeacherMapList(username);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //teacher页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getTeacherMapList，返回所有学生数据，
    @PostMapping("/teacherQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse teacherQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Teacher页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/teacherDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Teacher s= null;
        Optional<Teacher> op;
        if(id != null) {
            op= teacherRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            //删除教师前先提前删除与教师相关的各种信息
            List<Course> course = courseRepository.findCourseListByTeacherId(id);
            for (int i = 0; i < course.size(); i++) {
                Course c = course.get(i);
                List<Score> score = scoreRepository.findScoreListByCourseId(c.getId());
                for (int j = 0; j < score.size(); j++) {
                    scoreRepository.delete(score.get(j));
                }
                courseRepository.delete(c);
            }
            List<Paper> paper = paperRepository.findPaperListByTeacherId(id);
            for (int i = 0; i < paper.size(); i++) {
                paperRepository.delete(paper.get(i));
            }

            List<User> user = userRepository.findUserListByTeacherId(id);
            for (int i = 0; i< user.size(); i++) {
                userRepository.delete(user.get(i));
            }
            teacherRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //teacherEdit初始化方法
    //teacherEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/teacherEditInit")
    @PreAuthorize("hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse teacherEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Teacher s= null;
        Optional<Teacher> op;
        if(id != null) {
            op= teacherRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        List sexList = new ArrayList();
        Map m;
        m = new HashMap();
        m.put("label","男");
        m.put("value","1");
        sexList.add(m);
        m = new HashMap();
        m.put("label","女");
        m.put("value","2");
        sexList.add(m);
        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("teacherNum",s.getTeacherNum());
            form.put("teacherName",s.getTeacherName());
            form.put("sex",s.getSex());  //这里不需要转换
            form.put("dept",s.getDept());
            form.put("age",s.getAge());
            form.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd")); //这里需要转换为字符串
            form.put("EMail", s.getEMail());
            form.put("researchDirection", s.getResearchDirection());
            form.put("telephone", s.getTelephone());
            form.put("personalSignature",s.getPersonalSignature());
            form.put("selfAssessment", s.getSelfAssessment());
            form.put("courses", s.getCourses());
            image0  = getPersonImageString(attachFolder,s.getTeacherNum(), "0");
        }
        form.put("sexList",sexList);
        Map data = new HashMap();
        data.put("form",form);
        data.put("sexList",sexList);
        data.put("image0",image0);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Teacher 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewTeacherId(){
        Integer
                id = teacherRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/teacherEditSubmit")
    @PreAuthorize(" hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse teacherEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String teacherNum = CommonMethod.getString(form,"teacherNum");  //Map 获取属性的值
        String teacherName = CommonMethod.getString(form,"teacherName");
        String sex = CommonMethod.getString(form,"sex");
        String dept = CommonMethod.getString(form, "dept");
        Date birthday = CommonMethod.getDate(form,"birthday");
        String EMail = CommonMethod.getString(form, "EMail");
        String researchDirection = CommonMethod.getString(form, "researchDirection");
        String telephone = CommonMethod.getString(form, "telephone");
        String selfAssessment = CommonMethod.getString(form, "selfAssessment");
        String personalSignature = CommonMethod.getString(form, "personalSignature");
        String courses = CommonMethod.getString(form, "courses");

        if (Objects.equals(teacherName, "") || Objects.equals(teacherNum, "")) {
            return CommonMethod.getReturnMessageError("姓名或者教师号不能为空");
        }
        if (id == null && teacherRepository.findByTeacherNum(teacherNum).isPresent()) {
            return CommonMethod.getReturnMessageError("提交失败，教师号重复！");
        }
        Integer age;
        if (birthday != null) {
            Date now = new Date();
            long d1 = now.getTime();
            long d2 = birthday.getTime();
            age = (int)((d1 - d2) / 1000 / 60 / 60 / 24 / 365);
        }else {
            age = null;
        }

        Teacher s= null;
        Optional<Teacher> op;
        if(id != null) {
            op= teacherRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Teacher();   //不存在 创建实体对象
            id = getNewTeacherId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setTeacherNum(teacherNum);  //设置属性
        s.setTeacherName(teacherName);
        s.setSex(sex);
        s.setAge(age);
        s.setDept(dept);
        s.setBirthday(birthday);
        s.setResearchDirection(researchDirection);
        s.setEMail(EMail);
        s.setTelephone(telephone);
        s.setSelfAssessment(selfAssessment);
        s.setCourses(courses);
        s.setPersonalSignature(personalSignature);
        teacherRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }


    public String getPersonImageString(String attachFolder, String studentNum, String no) {
        String fileName =attachFolder + "images/" + studentNum + "-" + no + ".JPG";
        File file = new File(fileName);
        if (!file.exists())
            return "";
        try {
            FileInputStream in = new FileInputStream(file);
            int size = (int) file.length();
            byte data[] = new byte[size];
            in.read(data);
            in.close();
            String imgStr = "data:image/png;base64,";
            String s = new String(Base64.getEncoder().encode(data));
            imgStr = imgStr + s;
            return imgStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    @PostMapping("/teacherHomePageInit")
    @PreAuthorize("hasRole('USER2')")
    public DataResponse teacherHomePageInit(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        Teacher s= null;
        Optional<Teacher> op;
        if(username != null) {
            op= teacherRepository.findByTeacherNum(username);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        List paperList = getPaperMapList(s.getId());
        //这里获取List
        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("id", s.getId());
            form.put("teacherName",s.getTeacherName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                form.put("sex","男");
            }else {
                form.put("sex","女");
            }
            form.put("age",s.getAge());
            form.put("dept",s.getDept());
            form.put("teacherNum", s.getTeacherNum());
            form.put("telephone", s.getTelephone());
            form.put("EMail", s.getEMail());
            form.put("researchDirection", s.getResearchDirection());
            form.put("personalSignature",s.getPersonalSignature());
            form.put("courses", s.getCourses());
            image0  = getPersonImageString(attachFolder,s.getTeacherNum(), "0");
        }
        Map data = new HashMap();
        data.put("paperList", paperList);
        data.put("form",form);
        data.put("image0",image0);

        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }

    public List getPaperMapList(Integer teacherId) {
        List dataList = new ArrayList();
        List<Paper> sList = paperRepository.findPaperListByTeacherId(teacherId); //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Paper s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("paperName",s.getPaperName());
            m.put("teacher",s.getTeacher().getTeacherName());
            m.put("info",s.getInfo());
            m.put("publishTime", DateTimeTool.parseDateTime(s.getPublishTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    private String getCourses(Integer teacherId) {
        String str = "";
        List<Course> courseList = courseRepository.findCourseListByTeacherId(teacherId);
        if (courseList.size() == 0)
            return "";
        str = courseList.get(0).getCourseName();
        for (int i = 1; i < courseList.size(); i++) {
            str += "," + courseList.get(i).getCourseName();
        }
        return str;
    }

}

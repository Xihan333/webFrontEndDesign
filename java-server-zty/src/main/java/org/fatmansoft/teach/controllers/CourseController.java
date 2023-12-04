package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.repository.CourseRepository;
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
import java.util.zip.ZipOutputStream;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class CourseController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用CourseRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， courseRepository 相当于CourseRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Value("${attach.folder}")
    private String attachFolder;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private SessionsRepository sessionsRepository;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    //getCourseMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("courseNum",s.getCourseNum())， courseNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getCourseMapList(String numName) {
        List dataList = new ArrayList();
        List<Course> sList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Course s;
        Map m;
        String courseParas,courseNameParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("courseNum",s.getCourseNum());
            m.put("courseName",s.getCourseName());
            m.put("teacher",s.getTeacher().getTeacherName());
            m.put("credit",s.getCredit());
            m.put("preCourse",s.getPreCourse());
            m.put("location",s.getLocation());
            m.put("info",s.getInfo());
            m.put("teacherNum",s.getTeacher().getTeacherNum());
            m.put("numOfStudent", s.getNumOfStudent());
            m.put("deadline", DateTimeTool.parseDateTime(s.getDeadline(),"yyyy-MM-dd"));
            if (s.getSessions() != null)
                m.put("sessions","周" + s.getSessions().getWeek() + "，第" + s.getSessions().getDay() + "节");
            m.put("maxNumOfStudent", s.getMaxNumOfStudent());
            dataList.add(m);
        }
        return dataList;
    }

    public List getCourseMapList2(Integer teacherId) {
        List dataList = new ArrayList();
        List<Course> sList = courseRepository.findCourseListByTeacherId(teacherId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Course s;
        Map m;
        String courseParas,courseNameParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("courseNum",s.getCourseNum());
            m.put("courseName",s.getCourseName());
            m.put("teacher",s.getTeacher().getTeacherName());
            m.put("credit",s.getCredit());
            m.put("preCourse",s.getPreCourse());
            m.put("location",s.getLocation());
            m.put("info",s.getInfo());
            m.put("teacherNum",s.getTeacher().getTeacherNum());
            m.put("numOfStudent", s.getNumOfStudent());
            m.put("deadline", DateTimeTool.parseDateTime(s.getDeadline(),"yyyy-MM-dd"));
            if (s.getSessions() != null)
                m.put("sessions","周" + s.getSessions().getWeek() + "，第" + s.getSessions().getDay() + "节");
            m.put("maxNumOfStudent", s.getMaxNumOfStudent());
            dataList.add(m);
        }
        return dataList;
    }
    //course页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getCourseMapList，返回所有学生数据，
    @PostMapping("/courseInit")
    @PreAuthorize("hasRole('USER') or hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse courseInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getCourseMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/courseInit2")
    @PreAuthorize("hasRole('USER2')")
    public DataResponse courseInit2(@Valid @RequestBody DataRequest dataRequest) {
        String teacherNum =dataRequest.getString("teacherNum");
        Integer teacherId =  teacherRepository.findByTeacherNum(teacherNum).get().getId();
        List dataList = getCourseMapList2(teacherId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //course页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getCourseMapList，返回所有学生数据，
    @PostMapping("/courseQuery")
    @PreAuthorize("hasRole('USER2') or hasRole('USER') or hasRole('ADMIN')")
    public DataResponse courseQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getCourseMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Course页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/courseDelete")
    @PreAuthorize("hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Course s= null;
        List<Score> scoreList = null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                scoreList = scoreRepository.findScoreListByCourseId(s.getId());
            }

        }
        if(s != null) {
            if (!scoreList.isEmpty()){//删除与之关联的成绩表
                for (Score score : scoreList) {
                    scoreRepository.delete(score);
                }
            }
            courseRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //courseEdit初始化方法
    //courseEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/courseEditInit")
    @PreAuthorize("hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse courseEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course s= null;
        Sessions se = null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map m;
        int i;
        List sessionsIdList = new ArrayList();
        List<Sessions> sList = sessionsRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            se =sList.get(i);
            m = new HashMap();
            m.put("label","周" + se.getWeek() + ",第" + se.getDay() + "节");
            m.put("value",se.getId());
            sessionsIdList.add(m);
        }
        Map form = new HashMap();
        String image0="";
        form.put("sessionsIdList","");
        if(s != null) {
            form.put("courseNum",s.getCourseNum());
            form.put("courseName",s.getCourseName());
            form.put("credit",s.getCredit());
            form.put("preCourse",s.getPreCourse());
            form.put("teacherNum", s.getTeacher().getTeacherNum());
            form.put("location",s.getLocation());
            form.put("info",s.getInfo());
            form.put("deadline", DateTimeTool.parseDateTime(s.getDeadline(),"yyyy-MM-dd"));
            if (s.getSessions() != null)
                form.put("sessionsId",s.getSessions().getId());
            form.put("maxNumOfStudent", s.getMaxNumOfStudent());
        }
        form.put("sessionsIdList",sessionsIdList);
        Map data = new HashMap();
        data.put("sessionsIdList",sessionsIdList);
        data.put("form",form);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Course 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewCourseId(){
        Integer
                id = courseRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/courseEditSubmit")
    @PreAuthorize(" hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse courseEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String courseNum;  //Map 获取属性的值
        String courseName = CommonMethod.getString(form,"courseName");
        Integer credit = CommonMethod.getInteger(form,"credit");
        String teacherNum = CommonMethod.getString(form, "teacherNum");
        String preCourse = CommonMethod.getString(form,"preCourse");
        String location = CommonMethod.getString(form, "location");
        String info = CommonMethod.getString(form, "info");
        Date deadline = CommonMethod.getDate(form,"deadline");
        Integer sessionsId = CommonMethod.getInteger(form, "sessionsId");
        Integer maxNumOfStudent = CommonMethod.getInteger(form, "maxNumOfStudent");
        Integer numOfStudent = 0;
        if (credit == null)
            return CommonMethod.getReturnMessageError("学分输入错误");
        if (maxNumOfStudent == null)
            return CommonMethod.getReturnMessageError("最大选课人数输入错误");
        if (sessionsId == null)
            return CommonMethod.getReturnMessageError("上课时间不能为空");

        Teacher t;
        if (teacherNum == null) {
            String username = dataRequest.getString("username");
            Optional<Teacher> ot = teacherRepository.findByTeacherNum(username);
            t = ot.get();
        } else {
            if (teacherRepository.findByTeacherNum(teacherNum).isEmpty())
                return CommonMethod.getReturnMessageError("此教师不存在");
            t = teacherRepository.findByTeacherNum(teacherNum).get();
        }

        Course s= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Course();   //不存在 创建实体对象
            id = getNewCourseId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        courseNum = "sd2022003" + id;
        s.setCourseNum(courseNum);  //设置属性
        s.setCourseName(courseName);
        s.setCredit(credit);
        s.setPreCourse(preCourse);
        s.setTeacher(t);
        s.setLocation(location);
        s.setInfo(info);
        s.setDeadline(deadline);
        s.setSessions(sessionsRepository.getById(sessionsId));
        s.setNumOfStudent(numOfStudent);
        s.setMaxNumOfStudent(maxNumOfStudent);
        courseRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

}

package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.FamilyMemberRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
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

public class ScoreController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;


    public List getScoreMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("teacherName", c.getTeacher().getTeacherName());
            m.put("score",sc.getScore());
            if (sc.getGradePoint() != null)
                m.put("gradePoint", String.format("%.2f", sc.getGradePoint()));
            dataList.add(m);
        }
        return dataList;
    }
    public List getScoreMapList(String numName) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("teacherName", c.getTeacher().getTeacherName());
            m.put("score",sc.getScore());
            if (sc.getGradePoint() != null)
                m.put("gradePoint", String.format("%.2f", sc.getGradePoint()));
            dataList.add(m);
        }
        return dataList;
    }
    public List getScoreMapListByStudentNum(String studentNum) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByStudentNum(studentNum);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("credit", c.getCredit());
            m.put("teacher", c.getTeacher().getTeacherName());
            m.put("location", c.getLocation());
            m.put("score",sc.getScore());
            m.put("teacherName", c.getTeacher().getTeacherName());
            m.put("sessions", "周" + c.getSessions().getWeek() + ",第" + c.getSessions().getDay() +"节");
            if (sc.getGradePoint() != null)
                m.put("gradePoint", String.format("%.2f", sc.getGradePoint()));
            dataList.add(m);
        }
        return dataList;
    }

    public List getStudentMapList(Integer courseId) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByCourseId(courseId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("credit", c.getCredit());
            m.put("teacherName", c.getTeacher().getTeacherName());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("age",s.getAge());
            m.put("dept",s.getDept());
            m.put("score",sc.getScore());
            if (sc.getGradePoint() != null)
                m.put("gradePoint", String.format("%.2f", sc.getGradePoint()));
            dataList.add(m);
        }
        return dataList;
    }
    public List getScoreMapListByInput(String input) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByInput(input);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("teacherName", c.getTeacher().getTeacherName());
            m.put("score",sc.getScore());
            if (sc.getGradePoint() != null)
                m.put("gradePoint", String.format("%.2f", sc.getGradePoint()));
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/myCourseInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse myCourseInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId= dataRequest.getInteger("studentId");
        List dataList = getScoreMapList(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/scoreInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getScoreMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/scoreQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreQuery(@Valid @RequestBody DataRequest dataRequest) {
        String input = dataRequest.getString("input");
        List dataList = getScoreMapListByInput(input);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/studentScoreInit")
    @PreAuthorize("hasRole('USER')")
    public DataResponse studentScoreInit(@Valid @RequestBody DataRequest dataRequest) {
        String studentNum = dataRequest.getString("studentNum");
        List dataList = getScoreMapListByStudentNum(studentNum);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/teacherScoreInit")
    @PreAuthorize("hasRole('USER2')")
    public DataResponse teacherScoreInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        List dataList = getStudentMapList(courseId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/scoreDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('USER2')")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Score s= null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            Course c = s.getCourse();
            Date d = new Date();
            if (c.getDeadline() != null) {
                if (c.getDeadline().getTime() < d.getTime()) {
                    return CommonMethod.getReturnMessageError("退选失败，已经超过了选课截止日期！");
                }
            }
            c.setNumOfStudent(c.getNumOfStudent() - 1);//推选则选课人数减一
            scoreRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/scoreEditInit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER2')")
    public DataResponse scoreEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Score sc= null;
        Student s;
        Course c;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for(i = 0; i <cList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("label",c.getCourseName() + "-" + c.getTeacher().getTeacherName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        form.put("courseId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("courseId",sc.getCourse().getId());
            form.put("score",sc.getScore());
        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        Map data = new HashMap();
        data.put("form",form);
        data.put("studentIdList",studentIdList);
        data.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/scoreEditSubmit")
    @PreAuthorize(" hasRole('ADMIN') or hasRole('USER2')")
    public DataResponse scoreEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        Integer score = CommonMethod.getInteger(form,"score");
        Double gradePoint = null;
        if (score == null) {
            gradePoint = 0.0;
        } else if (score <= 50) {
            gradePoint = 0.0;
        } else {
            gradePoint = (score - 50.0) / 10.0;
        }
        Score sc= null;
        Student s= null;
        Course c = null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Score();   //不存在 创建实体对象
            id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setScore(score);
        sc.setGradePoint(gradePoint);
        scoreRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }

    @PostMapping("/doScoreAdd1")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse doScoreAdd1(@Valid @RequestBody DataRequest dataRequest) {
        String studentUsername = dataRequest.getString("studentUsername");
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> oc = courseRepository.findById(courseId);
        Course c = null;
        Integer score = null;
        Score sc= null;
        Student s= null;
        Optional<Score> op;
        Integer id;
        sc = new Score();   //不存在 创建实体对象
        id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
        sc.setId(id);  //设置新的id
        s = studentRepository.findByStudentNum(studentUsername).get();
        if (oc.isPresent()) {
            c = oc.get();
        } else {
            return CommonMethod.getReturnMessageError("课程不存在");
        }
        if (c.getNumOfStudent() >= c.getMaxNumOfStudent()) {
            return CommonMethod.getReturnMessageError("抱歉，已到达最大选课人数");
        }
        if (scoreRepository.findByStudentCourse(studentUsername, courseId).isPresent()) {
            return CommonMethod.getReturnMessageError("您已经选过此课程了");
        }
        List<Score> scoreList = scoreRepository.findScoreListByStudentNum(studentUsername);
        for (int i = 0; i < scoreList.size(); i++) {
            if (Objects.equals(scoreList.get(i).getCourse().getSessions().getId(), c.getSessions().getId()))
                return CommonMethod.getReturnMessageError("选课失败，此时间段已有其他课程");
        }
        Date d = new Date();
        if (c.getDeadline() != null) {
            if (c.getDeadline().getTime() < d.getTime()) {
                return CommonMethod.getReturnMessageError("选课失败，超过了选课截止时间");
            }
        }
        sc.setStudent(s);  //设置属性
        sc.setCourse(c);
        c.setNumOfStudent(c.getNumOfStudent() + 1);
        sc.setScore(score);
        sc.setGradePoint(0.0);
        scoreRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnMessageOK("添加成功");  //
    }

    @PostMapping("/doScoreAdd2")
    @PreAuthorize("hasRole('USER2') or hasRole('ADMIN')")
    public DataResponse doScoreAdd2(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer score = dataRequest.getInteger("score");
        Score sc= null;
        Student s= null;
        Course c = null;
        Double gradePoint = null;
        if (score == null) {
            gradePoint = 0.0;
        } else if (score <= 50) {
            gradePoint = 0.0;
        } else {
            gradePoint = (score - 50.0) / 10.0;
        }
        Optional<Score> op;
        Integer id;
        sc = new Score();   //不存在 创建实体对象
        id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
        sc.setId(id);  //设置新的id
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setScore(score);
        sc.setGradePoint(gradePoint);
        scoreRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnMessageOK();  //
    }
}

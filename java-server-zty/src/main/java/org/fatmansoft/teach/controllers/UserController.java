package org.fatmansoft.teach.controllers;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.repository.UserRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class UserController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用UserRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， userRepository 相当于UserRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用

    //getUserMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("userNum",s.getUserNum())， userNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private  ScoreRepository scoreRepository;
    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private AwardRepository awardRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private SocialPracticeRepository socialPracticeRepository;
    @Autowired
    private UserTagRepository userTagRepository;
    @Autowired
    private TagRepository tagRepository;


    //Change Password
    @PostMapping("/changePwd")
    @PreAuthorize("hasRole('USER2') or hasRole('USER') or hasRole('ADMIN')")
    public DataResponse changePwd(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        String password = dataRequest.getString("password");
        Optional<User> u = userRepository.findByUserName(username);
        if (u.isEmpty()) {
            return CommonMethod.getReturnMessageError("此用户不存在");
        }
        User user;
        user = u.get();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return CommonMethod.getReturnMessageOK("修改成功");  //按照测试框架规范会送Map的list
    }

    public List getUserMapList(String username) {
        List dataList = new ArrayList();
        List<User> sList = userRepository.findUserListByUserName(username);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        User s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            int role = s.getUserType().getId();
            m = new HashMap();
            m.put("id", s.getUserId());
            m.put("userName",s.getUserName());
            if (role == 1) {
                m.put("role", "管理员");
            }else if (role == 2) {
                m.put("role", "学生");
            }else if (role == 3) {
                m.put("role", "老师");
            }
            m.put("lastLoginTime",s.getLastLoginTime());
            dataList.add(m);
        }
        return dataList;
    }


    //user页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getUserMapList，返回所有学生数据，
    @PostMapping("/userInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse userInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getUserMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }



    //  学生信息删除方法
    //User页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/userDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse userDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        System.out.println(id);
        User s= null;
        Optional<User> op;
        if(id != null) {
            op= userRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            if (s.getStudent() != null) {
                studentDelete(s.getStudent().getId());
            }else if (s.getTeacher() != null) {
                teacherDelete(s.getTeacher().getId());
            }
            userRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK("删除成功！");  //通知前端操作正常
    }
    public void studentDelete(Integer id) {
        Student s= null;
        Optional<Student> op;
        if(id != null) {
            op= studentRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            //删除相关数据
            List<Activity> activity = activityRepository.findActivityListByStudentId(id);
            for (int i = 0; i < activity.size(); i++) {
                activityRepository.delete(activity.get(i));
            }

            List<Award> award = awardRepository.findAwardListByStudentId(id);
            for (int i = 0; i < award.size(); i++) {
                awardRepository.delete(award.get(i));
            }

            List<Blog> blog = blogRepository.findBlogListByStudentId(id);
            for (int i = 0; i < blog.size(); i++) {
                blogRepository.delete(blog.get(i));
            }

            List<Evaluation> evaluation1 = evaluationRepository.findByStudentId(id);
            for (int i = 0; i < evaluation1.size(); i++) {
                evaluationRepository.delete(evaluation1.get(i));
            }
            List<Evaluation> evaluation2 = evaluationRepository.findEvaluationListByEvaluatorId(id);
            for (int i = 0; i < evaluation2.size(); i++) {
                evaluationRepository.delete(evaluation2.get(i));
            }

            List<Score> score = scoreRepository.findByStudentId(id);
            for (int i = 0; i < score.size(); i++) {
                scoreRepository.delete(score.get(i));
            }

            List<SocialPractice> socialPractice = socialPracticeRepository.findSocialPracticeListByStudentId(id);
            for (int i = 0; i < socialPractice.size(); i++) {
                socialPracticeRepository.delete(socialPractice.get(i));
            }

            List<UserTag> userTag1 = userTagRepository.findUserTagListByStudentId(id);
            for (int i = 0; i < userTag1.size(); i++) {
                userTagRepository.delete(userTag1.get(i));
            }

            List<UserTag> userTag2 = userTagRepository.findUserTagListByTaggerId(id);
            for (int i = 0; i < userTag2.size(); i++) {
                userTagRepository.delete(userTag2.get(i));
            }
            List<User> user = userRepository.findUserListByStudentId(id);
            for (int i = 0; i < user.size(); i++) {
                userRepository.delete(user.get(i));
            }
            studentRepository.delete(s);    //数据库永久删除
        }
    }
    public void teacherDelete(Integer id) {
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
                for (int j = 0; j < score.size(); i++) {
                    scoreRepository.delete(score.get(i));
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
    }

    @PostMapping("/userJudge")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('USER2')")
    public DataResponse userJudge(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        User s = userRepository.findUserListByUserName2(username).get();
        Map form = new HashMap();
        if(s != null) {
            form.put("username",s.getUserName());
            form.put("role", s.getUserType().getId());
        }
        Map data = new HashMap();
        data.put("form",form);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    //userEdit初始化方法
    //userEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/userEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse userEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        User s= null;
        Optional<User> op;
        if(id != null) {
            op= userRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if(s != null) {
            form.put("userName",s.getUserName());
            form.put("password",s.getPassword());
        }
        Map data = new HashMap();
        data.put("form",form);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewUserId(){
        Integer
                id = userRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new User 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    @PostMapping("/userEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse userEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String userName = CommonMethod.getString(form,"userName");
        String password = CommonMethod.getString(form,"password");
        User s= null;
        Optional<User> op;
        if(id != null) {
            op= userRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new User();   //不存在 创建实体对象
            id = getNewUserId(); //获取鑫的主键，这个是线程同步问题;
            System.out.println(id);
            s.setUserId(id);  //设置新的id
        }//设置属性
//        s.setUserType(new UserType(EUserType.ROLE_USER));
        s.setUserName(userName);
        s.setPassword(password);
        userRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getUserId());  // 将记录的id返回前端
    }

}

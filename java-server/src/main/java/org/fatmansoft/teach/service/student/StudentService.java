package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.*;
import org.fatmansoft.teach.models.system.EUserType;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.system.BaseService;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.FormatJudge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入

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
    private GradeRepository gradeRepository;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private SocialService socialService;

    @Autowired
    private SocialRepository socialRepository;


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

    public synchronized Integer getNewStudentId(){
        Integer  id = studentRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };


    /**
     * getMapFromStudent 将学生表属性数据转换复制MAp集合里
     */
    public Map getMapFromStudent(Student s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        if(s.getClazz()!=null) {
            if(s.getClazz().getGrade()!=null){
                m.put("gradeName",s.getClazz().getGrade().getGradeName());
            }

            if(s.getClazz().getCampus()!=null){
                m.put("campusName",s.getClazz().getCampus().getName());
            }
            m.put("className", s.getClazz().getClazzName());
        }
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        Optional<User> uOp = userRepository.findByUserId(p.getPersonId());  // 查询获得 user对象
        User u = uOp.get();
        m.put("lastLoginTime",u.getLastLoginTime());
        return m;
    }

    /**
     *  getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return dataList;
    }

    /**
     * getStudentScoreList 将Score对象列表集合转换成Score Map对象列表集合
     * @param sList
     * @return
     */
    public List getStudentScoreList(List<Score>sList){
        List list = new ArrayList();
        if(sList == null || sList.size() == 0)
            return list;
        Map m;
        TeacherCourse tc;
        for(Score s:sList){
            m = new HashMap();
            tc = s.getTeacherCourse();
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("scoreId",s.getScoreId());
            m.put("courseNum", tc.getCourse().getNum());
            m.put("courseName", tc.getCourse().getName());
            m.put("credit", tc.getCourse().getCredit());
            m.put("commonMark",s.getCommonMark());
            m.put("finalMark",s.getFinalMark());
            m.put("totalMark",s.getCommonMark()+s.getFinalMark());
            list.add(m);
        }
        return list;
    }

    /**
     * getStudentMarkList 计算学生的的成绩等级
     * @param sList 学生成绩列表
     * @return 成绩等级Map对象列表
     */
    public List getStudentMarkList(List<Score> sList){
        String title[]={"优","良","中","及格","不及格"};
        int count[]= new int[5];
        List list = new ArrayList();
        if(sList == null || sList.size() == 0)
            return list;
        Map m;
        for(Score s:sList){
            Integer mark = s.getCommonMark() + s.getFinalMark();
            if(mark >= 90)
                count[0]++;
            else if(mark >= 80)
                count[1]++;
            else if(mark >= 70)
                count[2]++;
            else if(mark >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for(int i = 0; i < 5;i++) {
            m = new HashMap();
            m.put("name", title[i]+"："+count[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }

    public DataResponse getStudentOptionItemList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        List dataList = new ArrayList();
        if(sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCourseOptionItemListByClazzId(DataRequest dataRequest) {
        Integer clazzId=dataRequest.getInteger("clazzId");
        List<Student> sList = studentRepository.findStudentListByClazzClazzId(clazzId);  //数据库查询操作
        List dataList = new ArrayList();
        if(sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getStudentOptionItemListByCourseId(DataRequest dataRequest) {
        Integer courseId=dataRequest.getInteger("courseId");
        List<Student> sList = studentRepository.findStudentListByCourseId(courseId);  //数据库查询操作
        List dataList = new ArrayList();
        if(sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return CommonMethod.getReturnData(dataList);
    }


    public DataResponse getStudentIntroduceData() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Map info = getMapFromStudent(s);  // 查询学生信息Map对象
        info.put("introduce", studentIntroduceService.getIntroduceDataMap(u.getPerson().getPersonId()));
        List<Score> sList = scoreRepository.findStudentResultScores(s.getStudentId()); //获得学生成绩对象集合
        Map data = new HashMap();
        data.put("info",info);
        data.put("achievementList",achievementService.getPassedAchievementMapList(s.getPerson().getNum()));
        data.put("acticityList",activityService.getActivityMapListByStudentId(s.getStudentId()));
        data.put("socialList",socialService.getSocialMapList(s.getPerson().getNum()));
        data.put("scoreList",getStudentScoreList(sList));
        data.put("markList",getStudentMarkList(sList));
        Double gpa = getGPA(s);
        data.put("gpa",gpa);
        return CommonMethod.getReturnData(data);//将前端所需数据保留Map对象里，返还前端
    }

    private Double getGPA(Student s) {
        List<Score> scoreList = scoreRepository.findStudentResultScores(s.getStudentId());
        Double gpa = 0.0;
        Double gpaTotal = 0.0;
        Double creditTotal = 0.0;
        List dataList = new ArrayList();
        if(scoreList == null || scoreList.size() == 0)
            return 0.0;
        for(int i = 0; i < scoreList.size();i++) {
            Integer credit = scoreList.get(i).getTeacherCourse().getCourse().getCredit();
            Integer score = scoreList.get(i).getCommonMark() + scoreList.get(i).getFinalMark();
            creditTotal += credit;
            gpaTotal += credit * score;
        }
        gpa = (gpaTotal/creditTotal-50)/10;
        String  str = String.format("%.2f",gpa);
        gpa = Double.parseDouble(str);
        return gpa;
    }

    public DataResponse studentEdit(DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s = sOp.get();
        Integer studentId = s.getStudentId();
        Person p = u.getPerson();

        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String card = CommonMethod.getString(form,"card");
        if(!(card.equals("")|| FormatJudge.IdCard(card))){
            return CommonMethod.getReturnMessageError("身份证号不合法，请重新输入！");
        }
        String birthday = CommonMethod.getString(form,"birthday");
        if((!birthday.equals(""))&&(!card.equals(""))&&(!FormatJudge.birthday(birthday,card))){
            return CommonMethod.getReturnMessageError("您选择的生日与身份证号不符，请重新选择！");
        }
        p.setCard(card);
        p.setGender(CommonMethod.getString(form,"gender"));
        p.setBirthday(birthday);
        p.setEmail(CommonMethod.getString(form,"email"));
        p.setPhone(CommonMethod.getString(form,"phone"));
        p.setAddress(CommonMethod.getString(form,"address"));
        p.setIntroduce(CommonMethod.getString(form,"introduce"));
        personRepository.save(p);  // 修改保存人员信息
        studentRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getStudentId(),"修改成功！");  // 将studentId返回前端
    }

    public DataResponse studentEditSave(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
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
        Student s= null;
        Person p;
        User u;
        Optional<Student> op;
        Integer personId;
        if(studentId != null) {
            op= studentRepository.findById(studentId);  //查询对应数据库中主键为id的值的实体对象
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
            p.setType("1");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            String password = encoder.encode("123456");
            u= new User();
            u.setUserId(getNewUserId());
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Student();   // 创建实体对象
            s.setStudentId(getNewStudentId());
            s.setPerson(p);
            studentRepository.saveAndFlush(s);  //插入新的Student记录
        }else {
            p = s.getPerson();
            personId = p.getPersonId();
        }
        if(!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User>uOp = userRepository.findByUserId(personId);
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
        p.setEmail(CommonMethod.getString(form,"email"));
        p.setPhone(CommonMethod.getString(form,"phone"));
        p.setAddress(CommonMethod.getString(form,"address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setClazz(null);
//        String campusName = CommonMethod.getString(form,"campusName");
//        String gradeName=CommonMethod.getString(form,"gradeName");
//        String className=CommonMethod.getString(form,"className");
//        Optional<Clazz> opC=clazzRepository.findByGradeNameAndClassNameAndCampusName(gradeName,className,campusName);
//        Clazz c;
//        if(opC.isPresent()){
//            c=opC.get();
//        }
//        else {
//            Optional<Grade> opG=gradeRepository.findByGradeName(gradeName);
//            Grade g;
//            if(opG.isPresent()){
//                g=opG.get();
//            }
//            else {
//                Integer gradeId=gradeService.getNewGradeId();
//                g=new Grade();
//                g.setGradeId(gradeId);
//                g.setGradeName(gradeName);
//                gradeRepository.saveAndFlush(g);
//            }
//            Integer classId=clazzService.getNewClazzId();
//            c=new Clazz();
//            c.setClazzId(classId);
//            c.setGrade(g);
//            c.setClazzName(className);
//            clazzRepository.saveAndFlush(c);
//        }
//        s.setClazz(c);
        studentRepository.save(s);  //修改保存学生信息
        return CommonMethod.getReturnData(s.getStudentId());  // 将studentId返回前端
    }

    public DataResponse getMyClassStudents() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer studentId = s.getStudentId();
        Integer clazzId = s.getClazz().getClazzId();
        List<Student> sList = studentRepository.findStudentListByClazzClazzId(clazzId);  //数据库查询操作
        List dataList = new ArrayList();
        if(sList == null || sList.size() == 0)
            return CommonMethod.getReturnData(dataList);
        for(int i = 0; i < sList.size();i++) {
            if(sList.get(i).getStudentId() != studentId){
                dataList.add(getMapFromStudent(sList.get(i)));
            }
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getMyInfo() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        return CommonMethod.getReturnData(getMapFromStudent(s));
    }

    public DataResponse getStudentIntroduceDataByStudentId(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Optional<Student> sOp= studentRepository.findByStudentId(studentId); // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Map info = getMapFromStudent(s);  // 查询学生信息Map对象
        info.put("introduce", studentIntroduceService.getIntroduceDataMap(s.getPerson().getPersonId()));
        List<Score> sList = scoreRepository.findStudentResultScores(s.getStudentId()); //获得学生成绩对象集合
        Map data = new HashMap();
        data.put("info",info);
        data.put("achievementList",achievementService.getPassedAchievementMapList(s.getPerson().getNum()));
        data.put("acticityList",activityService.getActivityMapListByStudentId(s.getStudentId()));
        data.put("socialList",socialService.getSocialMapList(s.getPerson().getNum()));
        data.put("scoreList",getStudentScoreList(sList));
        data.put("markList",getStudentMarkList(sList));
        Double gpa = getGPA(s);
        data.put("gpa",gpa);
        return CommonMethod.getReturnData(data);//将前端所需数据保留Map对象里，返还前端
    }
}

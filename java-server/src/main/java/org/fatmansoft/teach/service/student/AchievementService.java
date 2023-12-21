package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.student.Achievement;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.AchievementRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    public synchronized  Integer getNewAchievementId(){
        Integer id = achievementRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    /**
     * getMapFromAchievement 将学生成就属性数据转换复制MAp集合里
     */
    public Map getMapFromAchievement(Achievement achievement) {
        Map m = new HashMap();
        Student s;
        if (achievement == null)
            return m;
        m.put("achievementId",achievement.getAchievementId());
        m.put("achievementName", achievement.getName());
        m.put("level", achievement.getLevel());
        m.put("time", achievement.getTime());
        m.put("type", achievement.getType());
        m.put("content", achievement.getContent());
        m.put("status", achievement.getStatus());
        Integer status = achievement.getStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));//性别类型的值转换成数据类型名
        s = achievement.getStudent();
        if (s == null)
            return m;
        if (s != null) {
            m.put("personId", s.getPerson().getPersonId());
            m.put("num", s.getPerson().getNum());
            m.put("name", s.getPerson().getName());
            m.put("dept", s.getPerson().getDept());
            m.put("card", s.getPerson().getCard());
            String gender = s.getPerson().getGender();
            m.put("gender", s.getPerson().getGender());
            m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
            m.put("birthday", s.getPerson().getBirthday());  //时间格式转换字符串
            m.put("email", s.getPerson().getEmail());
            m.put("phone", s.getPerson().getPhone());
            m.put("address", s.getPerson().getAddress());
            m.put("introduce", s.getPerson().getIntroduce());
        }
        return m;
    }

    /**
     * getAchievementMapList 根据输入参数查询得到学生成就数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getAchievementMapList(String numName) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getPassedAchievementMapList(String numName) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findPassedAchievementListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getWaitingAchievementMapList() {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findWaitingAchievementList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getPassedAchievementMapList() {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findPassedAchievementList(); //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getFailedAchievementMapList() {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findFailedAchievementList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public List getAchievementMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Achievement> sList = achievementRepository.findAchievementByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromAchievement(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse getAchievementList(DataRequest dataRequest) {
        String numName= dataRequest.getString("numName"); //学号和姓名
        List dataList = getAchievementMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    public DataResponse getStudentAchievement() {
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
        List dataList = getAchievementMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    public DataResponse achievementDelete(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            achievementRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getAchievementInfo(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromAchievement(a)); //这里回传包含成就信息的Map对象
    }


    public DataResponse achievementStudentEditSave(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String achievementName = CommonMethod.getString(form,"achievementName");  //Map 获取属性的值
        String level = CommonMethod.getString(form,"level");  //Map 获取属性的值
        String type = CommonMethod.getString(form,"type");  //Map 获取属性的值
        String content = CommonMethod.getString(form,"content");  //Map 获取属性的值
        String time = CommonMethod.getString(form,"time");  //Map 获取属性的值
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Achievement a = null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            achievementId = getNewAchievementId(); //获取Achievement新的主键
            a = new Achievement();
            a.setAchievementId(achievementId);
        }
        a.setName(achievementName);
        a.setLevel(level);
        a.setContent(content);
        a.setType(type);
        a.setTime(time);
        a.setStudent(s);
        a.setStatus(0);
        achievementRepository.saveAndFlush(a);//插入新的Achievement记录
        return CommonMethod.getReturnData(a.getAchievementId());  // 将achievementId返回前端
    }


    public DataResponse achievementPass(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setStatus(1);
                achievementRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse achievementFail(DataRequest dataRequest) {
        Integer achievementId = dataRequest.getInteger("achievementId");  //获取achievement_id值
        Achievement a= null;
        Optional<Achievement> op;
        if(achievementId != null) {
            op= achievementRepository.findById(achievementId);   //查询获得实体对象
            if(op.isPresent()) {
                a = op.get();
            }
            if(a!=null){
                a.setStatus(2);
                achievementRepository.save(a);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


}


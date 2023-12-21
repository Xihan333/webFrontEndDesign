package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Score;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.teacher.TeacherCourse;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private CampusRepository campusRepository;

    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromScore(Score s) {
        Map m = new HashMap<>();
        m.put("teacherCourseId",s.getTeacherCourse().getId());
        m.put("scoreId", s.getScoreId());
        m.put("courseId",s.getTeacherCourse().getCourse().getCourseId());
        m.put("studentNum",s.getStudent().getPerson().getNum());
        m.put("studentName",s.getStudent().getPerson().getName());
        m.put("clazzName",s.getStudent().getClazz().getClazzName());
        m.put("gradeName",s.getStudent().getClazz().getGrade().getGradeName());
        m.put("courseNum",s.getTeacherCourse().getCourse().getNum());
        m.put("courseName",s.getTeacherCourse().getCourse().getName());
        m.put("credit",s.getTeacherCourse().getCourse().getCredit());
        m.put("commonMark",s.getCommonMark());
        m.put("finalMark",s.getFinalMark());
        m.put("isResult",s.getIsResult());
        m.put("ranking",s.getRanking());
        return m;
    }

    public List getScoreMapList(List<Score> list){
        List dataList = new ArrayList();
        if(list == null || list.size() == 0)
            return dataList;
        for(int i = 0; i < list.size();i++) {
            dataList.add(getMapFromScore(list.get(i)));
        }
        return dataList;
    }

    public DataResponse getMyCourseScores() {
        //获取当前用户（学生）的信息
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        //数据库查询操作
        List<Score> sList = scoreRepository.findScoreListByStudentId(studentId);
        List dataList = getScoreMapList(sList);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getTeacherCourseScores() {
        //获取当前用户（教师）的信息
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher teacher= sOp.get();
        Integer teacherId = teacher.getTeacherId();
        List<Score> sList = scoreRepository.findScoreListByTeachertId(teacherId);
        List dataList = getScoreMapList(sList);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getRank(DataRequest dataRequest) {
        Integer teacherCourseId = dataRequest.getInteger("teacherCourseId");
        //获取当前用户（学生）的信息
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        List<Score> sList = scoreRepository.findScoreListByTeacherCourseId(teacherCourseId);
        Optional<Score> scoreOptional = scoreRepository.findByStudentIdAndTeacherCourseId(studentId,teacherCourseId);
        Score score = null;
        if(scoreOptional == null){
            return CommonMethod.getReturnMessageError("无成绩!");
        }else{
            score = scoreOptional.get();
        }
        Integer myScore = score.getCommonMark() + score.getFinalMark();
        Integer myRank = 1;
        for (int i = 0; i < sList.size(); i++) {
            Integer itemScore = sList.get(i).getCommonMark() + sList.get(i).getFinalMark();
            if(sList.get(i).getStudent() != student){
                if(itemScore > myScore){
                    myRank++;
                }
            }
        }
        return CommonMethod.getReturnData(myRank);
    }
}

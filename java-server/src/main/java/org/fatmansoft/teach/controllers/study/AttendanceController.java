package org.fatmansoft.teach.controllers.study;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.study.Attendance;
import org.fatmansoft.teach.models.study.Course;
import org.fatmansoft.teach.models.study.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.study.AttendanceRepository;
import org.fatmansoft.teach.repository.study.CourseRepository;
import org.fatmansoft.teach.repository.study.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.study.AttendanceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    public synchronized Integer getNewAttendanceId(){
        Integer  id = attendanceRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    @PostMapping("/getAttendanceOptionItemListByStudentIdAndCourseId")
    public OptionItemList getAttendanceOptionItemListByStudentIdAndCourseId(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId=dataRequest.getInteger("studentId");
        Integer courseId=dataRequest.getInteger("courseId");
        List<Attendance> aList=new ArrayList<>();
        List<OptionItem> itemList = new ArrayList();
        if(studentId==null){
            List<Score> scoreList=scoreRepository.findByCourseCourseId(courseId);
            if(!scoreList.isEmpty()){
                Integer scoreId=scoreList.get(0).getScoreId();
                aList = attendanceRepository.findAttendanceListByScoreScoreId(scoreId);
            }
        }
        else {
            Optional<Score> opScore=scoreRepository.findByStudentStudentIdAndCourseCourseId(studentId,courseId);
            if(opScore.isPresent()){
                Score score= opScore.get();
                aList=attendanceRepository.findAttendanceListByScoreScoreId(score.getScoreId());
            }
        }
        for (Attendance a : aList) {
            itemList.add(new OptionItem(a.getId(), a.getDate(), a.getDate()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getAttendanceList")
    public DataResponse getAttendanceList(@Valid @RequestBody DataRequest dataRequest) {
        Integer gradeId = dataRequest.getInteger("gradeId");
        if (gradeId == null)
            gradeId = 0;
        Integer clazzId = dataRequest.getInteger("clazzId");
        if (clazzId == null)
            clazzId = 0;
        Integer studentId=0;
        Integer userId=dataRequest.getInteger("userId");
        if(userId!=null){
            Student student=null;
            Optional<Student> opS=studentRepository.findByUserId(userId);
            if(opS.isPresent()){
                student=opS.get();
            }
            if(student!=null){
                studentId=student.getStudentId();
            }
        }
        else {
            studentId=dataRequest.getInteger("studentId");
        }
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null)
            courseId = 0;
        String date = dataRequest.getString("date");
        if (date == null)
            date = "";
        List<Attendance> aList = attendanceRepository.findByGradeAndClazzAndStudentAndCourseAndHomework(gradeId,clazzId,studentId,courseId,date);//数据库查询操作
        List dataList = attendanceService.getAttendanceMapList(aList);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getAttendanceListByTeacherId")
    public DataResponse getAttendanceListByTeacherId(@Valid @RequestBody DataRequest dataRequest) {
        Integer teacherId=0;
        Integer userId=dataRequest.getInteger("userId");
        if(userId!=null){
            Teacher teacher=null;
            Optional<Teacher> opT=teacherRepository.findByUserId(userId);
            if(opT.isPresent()){
                teacher=opT.get();
            }
            if(teacher!=null){
                teacherId=teacher.getTeacherId();
            }
        }
        else {
            teacherId=dataRequest.getInteger("teacherId");
        }
        List<Attendance> aList = attendanceRepository.findAttendanceListByTeacherId(teacherId);//数据库查询操作
        List dataList = attendanceService.getAttendanceMapList(aList);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getByCourse")
    /*根据课程查询考勤
      所需参数 courseId
    */
    public DataResponse getByCourse(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId= dataRequest.getInteger("courseId");
        List<Attendance> dataList = attendanceRepository.findByScoreStudentStudentId(courseId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/addOrEdit")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员修改添加考勤
      所需参数 attendanceId scoreId time situation
    */
    public DataResponse addOrEdit(@Valid @RequestBody DataRequest dataRequest)  {
        Integer attendanceId=dataRequest.getInteger("attendanceId");
        Map form=dataRequest.getMap("form");
        String date = CommonMethod.getString(form,"date");
        String situation = CommonMethod.getString(form,"situation");

        //校验数据
        String studentNum=CommonMethod.getString(form,"studentNum");
        String courseNum=CommonMethod.getString(form,"courseNum");

        Optional<Student> opStudent=studentRepository.findByPersonNum(studentNum);
        if(!opStudent.isPresent()){
            return CommonMethod.getReturnMessageError("学生不存在，请检查学号是否正确！");
        }

        Optional<Course> opCourse=courseRepository.findByNum(courseNum);
        if(!opCourse.isPresent()){
            return CommonMethod.getReturnMessageError("课程不存在，请检查课序号是否正确！");
        }

        Optional<Score> opScore=scoreRepository.findByStudentStudentIdAndCourseCourseId(opStudent.get().getStudentId(),opCourse.get().getCourseId());
        if(!opScore.isPresent()){
            return CommonMethod.getReturnMessageError("学生未选课，请检查学号或课序号是否正确！");
        }

        Score score=opScore.get();
        Optional<Attendance>  opAttendance=attendanceRepository.findAttendanceByScoreIdAndDate(score.getScoreId(), date);
        if(attendanceId==null&&opAttendance.isPresent()){
            return CommonMethod.getReturnMessageError("考勤已存在，请勿重复添加！");
        }

        Attendance attendance;
        if(attendanceId!=null){
            attendance=attendanceRepository.findById(attendanceId).get();
        }
        else{
            attendance=new Attendance();
            attendance.setId(attendanceService.getNewAttendanceId());
            attendanceRepository.saveAndFlush(attendance);
        }
        attendance.setScore(score);
        attendance.setDate(date);
        attendance.setSituation(situation);
        attendanceRepository.save(attendance);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员删除考勤
      所需参数 attendanceId
     */
    public DataResponse delete(@Valid @RequestBody DataRequest dataRequest) {
        Integer attendanceId = dataRequest.getInteger("attendanceId");
        attendanceRepository.deleteById(attendanceId);
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

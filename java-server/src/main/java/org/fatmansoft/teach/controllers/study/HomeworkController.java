package org.fatmansoft.teach.controllers.study;

import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.study.Attendance;
import org.fatmansoft.teach.models.study.Course;
import org.fatmansoft.teach.models.study.Homework;
import org.fatmansoft.teach.models.study.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.study.CourseRepository;
import org.fatmansoft.teach.repository.study.HomeworkRepository;
import org.fatmansoft.teach.repository.study.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.study.AttendanceService;
import org.fatmansoft.teach.service.study.HomeworkService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private HomeworkService homeworkService;
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

    @PostMapping("/getHomeworkOptionItemListByStudentIdAndCourseId")
    public OptionItemList getHomeworkOptionItemListByStudentIdAndCourseId(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId=dataRequest.getInteger("studentId");
        Integer courseId=dataRequest.getInteger("courseId");
        List<Homework> hList=new ArrayList<>();
        List<OptionItem> itemList = new ArrayList();
        if(studentId==null){
            List<Score> scoreList=scoreRepository.findByCourseCourseId(courseId);
            if(!scoreList.isEmpty()){
                Integer scoreId=scoreList.get(0).getScoreId();
                hList = homeworkRepository.findHomeworkListByScoreScoreId(scoreId);
            }
        }
        else {
            Optional<Score> opScore=scoreRepository.findByStudentStudentIdAndCourseCourseId(studentId,courseId);
            if(opScore.isPresent()){
                Score score= opScore.get();
                hList=homeworkRepository.findHomeworkListByScoreScoreId(score.getScoreId());
            }
        }
        for (Homework h : hList) {
            itemList.add(new OptionItem(h.getId(), h.getName(), h.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getHomeworkListByTeacherId")
    public DataResponse getHomeworkListByTeacherId(@Valid @RequestBody DataRequest dataRequest) {
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
        List<Homework> hList = homeworkRepository.findHomeworkListByTeacherId(teacherId);
        List dataList = homeworkService.getHomeworkMapList(hList);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getHomeworkList")
    public DataResponse getHomeworkList(@Valid @RequestBody DataRequest dataRequest) {
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
        String homeworkName = dataRequest.getString("homeworkName");
        if (homeworkName == null)
            homeworkName = "";
        List<Homework> hList = homeworkRepository.findByGradeAndClazzAndStudentAndCourseAndHomework(gradeId,clazzId,studentId,courseId,homeworkName);//数据库查询操作
        List dataList = homeworkService.getHomeworkMapList(hList);
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/addOrEdit")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员修改添加作业
      所需参数 homeworkId scoreId name mark
    */
    public DataResponse addOrEdit(@Valid @RequestBody DataRequest dataRequest) {
        Integer homeworkId=dataRequest.getInteger("homeworkId");
        Map form=dataRequest.getMap("form");
        String homeworkName = CommonMethod.getString(form,"homeworkName");
        String mark = CommonMethod.getString(form,"mark");

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
        Optional<Homework>  opHomework=homeworkRepository.findHomeworkByScoreIdAndName(score.getScoreId(),homeworkName);
        if(homeworkId==null&&opHomework.isPresent()){
            return CommonMethod.getReturnMessageError("作业已存在，请勿重复添加！");
        }

        Homework homework;
        if(homeworkId!=null){
            homework=homeworkRepository.findById(homeworkId).get();
        }
        else{
            homework=new Homework();
            homework.setId(homeworkService.getNewHomeworkId());
            homeworkRepository.saveAndFlush(homework);
        }
        homework.setScore(score);
        homework.setName(homeworkName);
        homework.setMark(mark);
        homeworkRepository.save(homework);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    /*管理员删除作业
      所需参数 homeworkId
     */
    public DataResponse delete(@Valid @RequestBody DataRequest dataRequest) {
        Integer homeworkId = dataRequest.getInteger("homeworkId");
        homeworkRepository.deleteById(homeworkId);
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

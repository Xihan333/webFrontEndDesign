package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.student.Score;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.repository.student.CourseRepository;
import org.fatmansoft.teach.repository.student.ScoreRepository;
import org.fatmansoft.teach.service.student.ScoreService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/score")
public class ScoreController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    //获取某个学生的成绩 根据 gradeId clazzId userId  studentId courseId
    @PostMapping("/getScoreList")
    public DataResponse getScoreList(@Valid @RequestBody DataRequest dataRequest) {
        Integer gradeId = dataRequest.getInteger("gradeId");
        if(gradeId == null)
            gradeId = 0;
        Integer clazzId = dataRequest.getInteger("clazzId");
        if(clazzId == null)
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
        if(courseId == null)
            courseId = 0;
        List<Score> sList = scoreRepository.findByStudentAndCourseAndGradeAndClazz(studentId, courseId, gradeId, clazzId);  //数据库查询操作
        List dataList = scoreService.getScoreMapList(sList);
        return CommonMethod.getReturnData(dataList);
    }


    @PostMapping("/getScoreListByTeacherId")
    public DataResponse getScoreListByTeacherId(@Valid @RequestBody DataRequest dataRequest) {
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
        List<Score> sList = scoreRepository.findScoreListByTeacherId(teacherId );  //数据库查询操作
        List dataList = scoreService.getScoreMapList(sList);
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/scoreSave")
    public DataResponse scoreSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Map form = dataRequest.getMap("form");
        Integer commonMark = CommonMethod.getInteger(form, "commonMark");
        Integer finalMark = CommonMethod.getInteger(form,"finalMark");
        Score score = scoreRepository.findById(scoreId).get();
        score.setCommonMark(commonMark);
        score.setFinalMark(finalMark);
        score.setIsResult(1);
        scoreRepository.save(score);
        return CommonMethod.getReturnMessageOK();
    }
}

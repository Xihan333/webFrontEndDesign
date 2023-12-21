package org.fatmansoft.teach.controllers.teacher;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.*;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.StaticValueRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherCourseRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.student.CourseService;
import org.fatmansoft.teach.service.student.GradeService;
import org.fatmansoft.teach.service.teacher.TeacherCourseService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacherCourse")
public class TeacherCourseController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

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

    @Autowired
    private StaticValueRepository staticValueRepository;

    //添加课程
    @PostMapping("/addCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addCourse(@Valid @RequestBody DataRequest dataRequest) {
        return  teacherCourseService.addTeacherCourse(dataRequest);
    }

    //编辑课程
    @PostMapping("/editCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse editCourse(@Valid @RequestBody DataRequest dataRequest) {
        return  teacherCourseService.editTeacherCourse(dataRequest);
    }

    //删除课程 teacherCourseId
    @PostMapping("/deleteCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteCourse(@Valid @RequestBody DataRequest dataRequest) {
        return teacherCourseService.deleteTeacherCourse(dataRequest);
    }

}

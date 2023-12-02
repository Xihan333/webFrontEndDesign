package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.study.Attendance;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.repository.study.AttendanceRepository;
import org.fatmansoft.teach.service.study.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    AttendanceService attendanceService;
    @Autowired
    TeacherCourseRepository teacherCourseRepository;

    public void test(){
        List<Attendance> list=attendanceRepository.findByScoreStudentStudentId(1);
        //System.out.println(list.get(0).getDate());
        //attendanceRepository.deleteByScoreScoreId(3);
    }
}

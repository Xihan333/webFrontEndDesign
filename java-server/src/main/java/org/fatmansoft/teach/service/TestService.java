package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.TaskStudent;
import org.fatmansoft.teach.models.TeamWork;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TaskStudentRepository;
import org.fatmansoft.teach.repository.TeamWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TaskStudentRepository taskStudentRepository;
    @Autowired
    private TeamWorkRepository teamWorkRepository;
    public void test1(){
        List<Student> sList = studentRepository.findAll();
        TeamWork tw;
        int j;
        for(Student s:sList) {
            for(j = 1; j <= 8;j++) {
                tw = new TeamWork();
                tw.setStudent(s);
                tw.setWeek(j);
                tw.setCourseId(2);
                teamWorkRepository.save(tw);
            }
        }
        System.out.println();
    }
}

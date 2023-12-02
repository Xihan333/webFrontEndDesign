package org.fatmansoft.teach.service;

import org.fatmansoft.teach.repository.TeacherCourseRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherCourseService {
    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    public synchronized Integer getNewTeacherCourseId() {  //synchronized 同步方法
        Integer id = teacherCourseRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }
}
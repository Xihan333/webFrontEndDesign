package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.repository.student.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeService {
    @Autowired
    private GradeRepository gradeRepository;
    public synchronized Integer getNewGradeId(){  //synchronized 同步方法
        Integer  id = gradeRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
}

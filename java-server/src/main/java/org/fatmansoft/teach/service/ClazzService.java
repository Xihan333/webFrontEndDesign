package org.fatmansoft.teach.service;

import org.fatmansoft.teach.repository.ClazzRepository;
import org.fatmansoft.teach.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClazzService {
    @Autowired
    private ClazzRepository clazzRepository;
    public synchronized Integer getNewClazzId(){  //synchronized 同步方法
        Integer  id = clazzRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
}
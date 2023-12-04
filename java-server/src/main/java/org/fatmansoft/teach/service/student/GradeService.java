package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Grade;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.GradeRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public DataResponse getGradeOptionItemList() {
        List<Grade> gList = gradeRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Grade g : gList) {
            itemList.add(new OptionItem(g.getGradeId(), g.getGradeName(), g.getGradeName()));
        }
        return CommonMethod.getReturnData(itemList);
    }


}

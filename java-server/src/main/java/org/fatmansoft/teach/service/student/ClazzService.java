package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Clazz;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.ClazzRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public DataResponse getClazzOptionItemListByGradeId(DataRequest dataRequest) {
        Integer gradeId=dataRequest.getInteger("gradeId");
        List<Clazz> cList = clazzRepository.findClazzListByGradeGradeId(gradeId);  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Clazz c : cList) {
            itemList.add(new OptionItem(c.getClazzId(), c.getClazzName(), c.getClazzName()));
        }
        return CommonMethod.getReturnData(itemList);
    }


}

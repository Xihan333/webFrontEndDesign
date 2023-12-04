package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Score;
import org.fatmansoft.teach.repository.student.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoreService {
    @Autowired
    private ScoreRepository scoreRepository;
    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public Map getMapFromScore(Score s) {
        Map m=new HashMap<>();
        m.put("scoreId", s.getScoreId());
        m.put("studentNum",s.getStudent().getPerson().getNum());
        m.put("studentName",s.getStudent().getPerson().getName());
        m.put("clazzName",s.getStudent().getClazz().getClazzName());
        m.put("gradeName",s.getStudent().getClazz().getGrade().getGradeName());
        m.put("courseNum",s.getCourse().getNum());
        m.put("courseName",s.getCourse().getName());
        m.put("credit",s.getCourse().getCredit());
        m.put("commonMark",s.getCommonMark());
        m.put("finalMark",s.getFinalMark());
        return m;
    }

    public List getScoreMapList(List<Score> list){
        List dataList = new ArrayList();
        if(list == null || list.size() == 0)
            return dataList;
        for(int i = 0; i < list.size();i++) {
            dataList.add(getMapFromScore(list.get(i)));
        }
        return dataList;
    }
}

package org.fatmansoft.teach.service.dailyActivity;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Travel;
import org.fatmansoft.teach.repository.dailyActivity.TravelRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TravelService {

    @Autowired
    private TravelRepository travelRepository;

    /**
     * getMapFromTravel 将旅程属性数据转换复制MAp集合里
     */

    public Map getMapFromTravel(Travel travel) {
        Map m = new HashMap();
        Student s;
        if (travel == null)
            return m;
        m.put("day", travel.getDay());
        m.put("title", travel.getTitle());
        m.put("location", travel.getLocation());
        m.put("introduction", travel.getIntroduction());
        m.put("travelId", travel.getTravelId());
        s = travel.getStudent();
        if (s == null)
            return m;
        if(s != null){
            m.put("studentId", s.getStudentId());
            m.put("personId", s.getPerson().getPersonId());
            m.put("num", s.getPerson().getNum());
            m.put("name", s.getPerson().getName());
            m.put("dept", s.getPerson().getDept());
            m.put("card", s.getPerson().getCard());
            String gender = s.getPerson().getGender();
            m.put("gender", s.getPerson().getGender());
            m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
            m.put("birthday", s.getPerson().getBirthday());  //时间格式转换字符串
            m.put("email", s.getPerson().getEmail());
            m.put("phone", s.getPerson().getPhone());
            m.put("address", s.getPerson().getAddress());
            m.put("introduce", s.getPerson().getIntroduce());
        }
        return m;
    }

    /**
     * getTravelMapList 根据输入参数查询得到旅程数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */

    public List getTravelMapList(String dayLocation) {
        List dataList = new ArrayList();
        List<Travel> sList = travelRepository.findTravelListByNumName(dayLocation);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTravel(sList.get(i)));
        }
        return dataList;
    }

    public List getTravelMapListByStudentId(Integer studentId){
        List dataList = new ArrayList();
        List<Travel> sList = travelRepository.findTravelByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++){
            dataList.add(getMapFromTravel(sList.get(i)));
        }
        return dataList;
    }
}

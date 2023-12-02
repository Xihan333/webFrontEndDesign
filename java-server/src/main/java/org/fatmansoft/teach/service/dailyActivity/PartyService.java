package org.fatmansoft.teach.service.dailyActivity;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Party;
import org.fatmansoft.teach.repository.dailyActivity.PartyRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartyService {
    @Autowired
    private PartyRepository partyRepository;

    /**
     * getMapFromParty 将聚会属性数据转换复制MAp集合里
     */

    public Map getMapFromParty(Party party) {
        Map m = new HashMap();
        Student s;
        if (party == null)
            return m;
        m.put("day", party.getDay());
        m.put("title", party.getTitle());
        m.put("location", party.getLocation());
        m.put("introduction", party.getIntroduction());
        m.put("partyId", party.getPartyId());
        m.put("organizer",party.getOrganizer());
       s = party.getStudent();
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
           m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名m.put("birthday", s.getPerson().getBirthday());  //时间格式转换字符串m.put("email", s.getPerson().getEmail());
           m.put("phone", s.getPerson().getPhone());
           m.put("address", s.getPerson().getAddress());
           m.put("introduce", s.getPerson().getIntroduce());
       }
        return m;
    }



    /**
     * getPartyMapList 根据输入参数查询得到学生成就数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */

    public List getPartyMapList(String organizerLocation) {
        List dataList = new ArrayList();
        List<Party> sList = partyRepository.findPartyListByNumName(organizerLocation);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromParty(sList.get(i)));
        }
        return dataList;
    }

    public List getPartyMapListByStudentId(Integer studentId){
        List dataList = new ArrayList();
        List<Party> sList = partyRepository.findPartyByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size(); i++){
            dataList.add(getMapFromParty(sList.get(i)));
        }
        return dataList;
    }
}

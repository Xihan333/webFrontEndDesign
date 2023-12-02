package org.fatmansoft.teach.service;

import lombok.extern.slf4j.Slf4j;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Message;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Party;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.repository.FeeRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class FeeService {
    @Autowired
    private FeeRepository feeRepository;

    public Map getMapFromFee(Fee fee) {
        Map m = new HashMap();
        Student s;
        if (fee == null)
            return m;
        m.put("money", fee.getMoney());
        m.put("day", fee.getDay());
        m.put("feeId", fee.getFeeId());
        s = fee.getStudent();
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
    public List getFeeMapList(String numName) {
        List dataList = new ArrayList();
        List<Fee> sList = feeRepository.findFeeListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromFee(sList.get(i)));
        }
        return dataList;
    }

    public List getFeeMapListByStudentId(Integer studentId){
        List dataList = new ArrayList();
        List<Fee> sList = feeRepository.findFeeByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size(); i++){
            dataList.add(getMapFromFee(sList.get(i)));
        }
        return dataList;
    }
}

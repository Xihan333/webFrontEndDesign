package org.fatmansoft.teach.service.studentInfo;

import org.fatmansoft.teach.models.studentInfo.Punishment;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.studentInfo.PunishmentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PunishmentService {
    @Autowired
    private PunishmentRepository punishmentRepository;


    /**
     * getMapFromPunishment 将学生成就属性数据转换复制MAp集合里
     */
    public Map getMapFromPunishment(Punishment punishment) {
        Map m = new HashMap();
        Student s;
        if(punishment == null)
            return m;
        m.put("punishmentId",punishment.getPunishmentId());
        m.put("punishmentName",punishment.getName());
        m.put("time",punishment.getTime());
        m.put("content",punishment.getContent());
        s = punishment.getStudent();
        if(s == null)
            return m;
        m.put("studentId", s.getStudentId());
        m.put("personId", s.getPerson().getPersonId());
        m.put("num",s.getPerson().getNum());
        m.put("name",s.getPerson().getName());
        m.put("dept",s.getPerson().getDept());
        m.put("card",s.getPerson().getCard());
        String gender = s.getPerson().getGender();
        m.put("gender",s.getPerson().getGender());
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", s.getPerson().getBirthday());  //时间格式转换字符串
        m.put("email",s.getPerson().getEmail());
        m.put("phone",s.getPerson().getPhone());
        m.put("address",s.getPerson().getAddress());
        m.put("introduce",s.getPerson().getIntroduce());
        return m;
    }

    /**
     *  getPunishmentMapList 根据输入参数查询得到学生成就数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getPunishmentMapList(String numName) {
        List dataList = new ArrayList();
        List<Punishment> sList = punishmentRepository.findPunishmentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromPunishment(sList.get(i)));
        }
        return dataList;
    }

    public List getPunishmentMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Punishment> sList = punishmentRepository.findPunishmentByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromPunishment(sList.get(i)));
        }
        return dataList;
    }

}

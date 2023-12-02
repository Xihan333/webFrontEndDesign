package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Message;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.dailyActivity.Party;
import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.repository.MessageRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Map getMapFromMessage(Message message) {
        Map m = new HashMap();
        Student s;
        if (message == null)
            return m;
        m.put("place", message.getPlace());
        m.put("start", message.getStart());
        m.put("end", message.getEnd());
        m.put("messageId", message.getMessageId());
        m.put("status", message.getStatus());
        Integer status = message.getStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));//性别类型的值转换成数据类型名
        s = message.getStudent();
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

    public List getMessageMapList(String numName) {
        List dataList = new ArrayList();
        List<Message> sList = messageRepository.findMessageListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromMessage(sList.get(i)));
        }
        return dataList;
    }
    public List getMessageMapListByStudentId(Integer studentId){
        List dataList = new ArrayList();
        List<Message> sList = messageRepository.findMessageByStudentId(studentId);
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size(); i++){
            dataList.add(getMapFromMessage(sList.get(i)));
        }
        return dataList;
    }

    public List getWaitingMessageMapList() {
        List dataList = new ArrayList();
        List<Message> sList = messageRepository.findWaitingMessageList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromMessage(sList.get(i)));
        }
        return dataList;
    }

    public List getPassedMessageMapList() {
        List dataList = new ArrayList();
        List<Message> sList = messageRepository.findPassedMessageList(); //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromMessage(sList.get(i)));
        }
        return dataList;
    }

    public List getFailedMessageMapList() {
        List dataList = new ArrayList();
        List<Message> sList = messageRepository.findFailedMessageList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromMessage(sList.get(i)));
        }
        return dataList;
    }
}

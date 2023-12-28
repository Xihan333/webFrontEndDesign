package org.fatmansoft.teach.service.teacher;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.FormatJudge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入

    @Autowired
    private PersonRepository personRepository;

    public synchronized Integer getNewTeacherId() {  //synchronized 同步方法
        Integer id = teacherRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    public Map getMapFromTeacher(Teacher s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        m.put("title",s.getTitle());
        m.put("degree",s.getDegree());
        m.put("direction",s.getDirection());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("teacherId", s.getTeacherId());
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    public List getTeacherMapList(String numName) {
        List dataList = new ArrayList();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        for(int i = 0; i < sList.size();i++) {
            dataList.add(getMapFromTeacher(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse getMyInfo() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        return CommonMethod.getReturnData(getMapFromTeacher(s));
    }

    public DataResponse teacherEdit(DataRequest dataRequest) {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Teacher> sOp= teacherRepository.findByUserId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("教师不存在！");
        Teacher s= sOp.get();
        Person p = s.getPerson();
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String card = CommonMethod.getString(form,"card");
        if(!(card.equals("")|| FormatJudge.IdCard(card))){
            return CommonMethod.getReturnMessageError("身份证号不合法，请重新输入！");
        }
        String birthday = CommonMethod.getString(form,"birthday");
        if((!birthday.equals(""))&&(!card.equals(""))&&(!FormatJudge.birthday(birthday,card))){
            return CommonMethod.getReturnMessageError("您选择的生日与身份证号不符，请重新选择！");
        }
        p.setCard(card);
        p.setGender(CommonMethod.getString(form,"gender"));
        p.setBirthday(birthday);
        p.setEmail(CommonMethod.getString(form,"email"));
        p.setPhone(CommonMethod.getString(form,"phone"));
        p.setAddress(CommonMethod.getString(form,"address"));
        s.setDirection(CommonMethod.getString(form,"direction"));
        s.setDegree(CommonMethod.getString(form,"degree"));
        s.setTitle(CommonMethod.getString(form,"title"));
        p.setIntroduce(CommonMethod.getString(form,"introduce"));
        personRepository.save(p);  // 修改保存人员信息
        teacherRepository.save(s);
        return CommonMethod.getReturnData(s.getTeacherId(),"修改成功！");  // 将studentId返回前端
    }
}

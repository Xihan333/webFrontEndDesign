package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.*;
import org.fatmansoft.teach.models.student.Clazz;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.CampusRepository;
import org.fatmansoft.teach.repository.student.ClazzRepository;
import org.fatmansoft.teach.repository.student.GradeRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClazzService {

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入

    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入

    @Autowired
    private UserRepository userRepository;  //学生用户数据操作自动注入

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private CampusRepository campusRepository;

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

    public DataResponse clazzEditSave(DataRequest dataRequest) {
        Integer clazzId = dataRequest.getInteger("clazzId");  //获取clazz_id值
        Map clazz = dataRequest.getMap("clazz");
        String clazzName = CommonMethod.getString(clazz,"clazzName");
        Integer gradeId = CommonMethod.getInteger(clazz,"gradeId");
        Integer campusId = CommonMethod.getInteger(clazz,"campusId");
        Clazz a = null;
        Optional<Clazz> op;
        if(clazzId != null) {
            op= clazzRepository.findByClazzId(clazzId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null){
            clazzId = getNewClazzId(); //获取Clazz新的主键
            a = new Clazz();
            a.setClazzId(clazzId);
        }
        Optional<Grade> og = gradeRepository.findByGradeId(gradeId);
        Grade grade;
        if(og.isPresent()){
            grade = og.get();
        }else{
            return CommonMethod.getReturnMessageError("年级不存在！");
        }
        Optional<Campus> oc = campusRepository.findByCampusId(campusId);
        Campus campus;
        if(oc.isPresent()){
            campus = oc.get();
        }else{
            return CommonMethod.getReturnMessageError("学院不存在！");
        }
        clazzRepository.saveAndFlush(a);//插入新的Clazz记录
        return CommonMethod.getReturnData(a.getClazzId(),"修改或新增成功");  // 将ClazzId返回前端
    }

    public DataResponse clazzDelete(DataRequest dataRequest) {
        Integer clazzId = dataRequest.getInteger("clazzId");  //获取clazz_id值
        Clazz a = null;
        Optional<Clazz> op;
        if(clazzId != null) {
            op= clazzRepository.findByClazzId(clazzId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }else{
                return CommonMethod.getReturnMessageError("班级不存在！");
            }
        }
        if(a != null) {
            List<Student> studentList = studentRepository.findStudentListByClazzClazzId(clazzId);
            for(Student s: studentList){
                s.setClazz(null);
            }
            clazzRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK("删除成功");  //通知前端操作正常
    }
}

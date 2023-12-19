package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Evaluation;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.EvaluationRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvaluationService {

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入

    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入

    @Autowired
    private UserRepository userRepository;  //学生用户数据操作自动注入

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EvaluationService evaluationService;

    public synchronized Integer getNewEvaluationId(){
        Integer id = evaluationRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    public DataResponse getEvaluationList() {
        List dataList = evaluationService.getEvaluationMapList();
        return CommonMethod.getReturnData(dataList);
    }

    public Map getMapFromEvaluation(Evaluation evaluation) {
        Map m = new HashMap();
        Student student,evaluator;
        if (evaluation == null)
            return m;
        m.put("evaluationId",evaluation.getEvaluationId());
        m.put("evalTime", evaluation.getEvalTime());
        m.put("eval", evaluation.getEval());
        student = evaluation.getStudent();
        evaluator = evaluation.getEvaluator();
        m.put("student",student);
        m.put("evaluator",evaluator);
//        if (student == null)
//            return m;
//        if (student != null) {
//            m.put("personId", student.getPerson().getPersonId());
//            m.put("num", student.getPerson().getNum());
//            m.put("name", student.getPerson().getName());
//            m.put("className",student.getClazz().getClazzName());
//        }
//        m.put("evaluatorId",evaluator.getStudentId());
        return m;
    }

    public List getEvaluationMapList() {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromEvaluation(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse evaluationEditSave(DataRequest dataRequest) {
        Integer evaluationId = dataRequest.getInteger("evaluationId");  //获取evaluation_id值
        Map evaluation = dataRequest.getMap("evaluation");
        String eval = CommonMethod.getString(evaluation,"eval");
        Integer studentId = dataRequest.getInteger("studentId");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student evaluator= sOp.get();
        Integer evaluatorId = evaluator.getStudentId();

        Optional<Student> sOp0= studentRepository.findByStudentId(studentId);  // 查询获得 Student对象
        if(!sOp0.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student = sOp0.get();
        Evaluation a = null;
        Optional<Evaluation> op;
        if(evaluationId != null) {
            op= evaluationRepository.findByEvaluationId(evaluationId);  //查询对应数据库中主键为id的值的实体对象
        }
        if(a == null){
            evaluationId = getNewEvaluationId(); //获取Evaluation新的主键
            a = new Evaluation();
            a.setEvaluationId(evaluationId);
            a.setEvalTime(DateTimeTool.parseDateTime(new Date()));
        }
        a.setEval(eval);
        a.setStudent(student);
        a.setEvaluator(evaluator);
        evaluationRepository.saveAndFlush(a);//插入新的Evaluation记录
        return CommonMethod.getReturnData(a.getEvaluationId(),"修改或新增成功");  // 将EvaluationId返回前端
    }

    public DataResponse getMyEvaluationList() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer studentId = s.getStudentId();
        List dataList = evaluationService.getEvaluationMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    private List getEvaluationMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findEvaluationByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromEvaluation(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse evaluationDelete(DataRequest dataRequest) {
        Integer evaluationId = dataRequest.getInteger("evaluationId");  //获取evaluation_id值
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        Evaluation a = null;
        Optional<Evaluation> op;
        if(evaluationId != null) {
            op= evaluationRepository.findByEvaluationId(evaluationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
                if(a.getEvaluator().getStudentId() != student.getStudentId()){
                    return CommonMethod.getReturnMessageError("删除失败，无法删除他人的博客！");
                }
            }
        }
        if(a != null) {
            evaluationRepository.delete(a);//删除该条
        }
        return CommonMethod.getReturnMessageOK("删除成功");  //通知前端操作正常
    }

    public DataResponse getOneEvaluationList(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        List dataList = evaluationService.getEvaluationMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getMySendEvaluationList() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer studentId = s.getStudentId();
        List dataList = evaluationService.getEvaluationMapListByEvaluatorId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    private List getEvaluationMapListByEvaluatorId(Integer studentId) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findEvaluationByEvaluatorId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromEvaluation(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse evaluationAdminDelete(DataRequest dataRequest) {
        Integer evaluationId = dataRequest.getInteger("evaluationId");  //获取evaluation_id值
        Evaluation a = null;
        Optional<Evaluation> op;
        if(evaluationId != null) {
            op = evaluationRepository.findByEvaluationId(evaluationId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a != null) {
            evaluationRepository.delete(a);//删除该条
        }
        return CommonMethod.getReturnMessageOK("删除成功");  //通知前端操作正常
    }
}

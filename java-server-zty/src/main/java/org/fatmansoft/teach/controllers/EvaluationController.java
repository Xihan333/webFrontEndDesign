package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class EvaluationController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;


    public List getEvaluationMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Evaluation sc;
        Student s;
        Student c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getEvaluator();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("evaluatorNum",c.getStudentNum());
            m.put("evaluatorName",c.getStudentName());
            m.put("eval",sc.getEval());
            m.put("evalTime", DateTimeTool.parseDateTime(sc.getEvalTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    public List getEvaluationMapList(String numName) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Evaluation sc;
        Student s;
        Student c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getEvaluator();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("evaluatorNum",c.getStudentNum());
            m.put("evaluatorName",c.getStudentName());
            m.put("eval",sc.getEval());
            m.put("evalTime", DateTimeTool.parseDateTime(sc.getEvalTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    public List getEvaluationMapListByEvaluatorNum(String evaluatorNum) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findEvaluationListByEvaluatorNum(evaluatorNum);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Evaluation sc;
        Student s;
        Student c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getEvaluator();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("evaluatorNum",c.getStudentNum());
            m.put("evaluatorName",c.getStudentName());
            m.put("eval",sc.getEval());
            m.put("evalTime", DateTimeTool.parseDateTime(sc.getEvalTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    public List getEvaluationMapListByStudentNum(String studentNum) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findEvaluationListByStudentNum(studentNum);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Evaluation sc;
        Student s;
        Student c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getEvaluator();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("evaluatorNum",c.getStudentNum());
            m.put("evaluatorName",c.getStudentName());
            m.put("eval",sc.getEval());
            m.put("evalTime", DateTimeTool.parseDateTime(sc.getEvalTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }



    public List getEvaluationMapListByEvaluatorId(String input) {
        List dataList = new ArrayList();
        List<Evaluation> sList = evaluationRepository.findEvaluationListByInput(input);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Evaluation sc;
        Student s;
        Student c;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getEvaluator();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("evaluatorNum",c.getStudentNum());
            m.put("evaluatorName",c.getStudentName());
            m.put("eval",sc.getEval());
            m.put("evalTime", DateTimeTool.parseDateTime(sc.getEvalTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/studentEvaluationInit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse studentEvaluationInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId= dataRequest.getInteger("studentId");
        List dataList = getEvaluationMapList(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/evaluationInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse evaluationInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getEvaluationMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/evaluatorEvaluationInit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse evaluatorEvaluationInit(@Valid @RequestBody DataRequest dataRequest) {
        String evaluatorNum = dataRequest.getString("evaluatorNum");
        List dataList = getEvaluationMapListByEvaluatorNum(evaluatorNum);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/evaluatorEvaluationQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse evaluatorEvaluationQuery(@Valid @RequestBody DataRequest dataRequest) {
        String input= dataRequest.getString("input");
        List dataList = getEvaluationMapListByEvaluatorId(input);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/QueryEvaluationByStudentNum")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse QueryEvaluationByStudentNum(@Valid @RequestBody DataRequest dataRequest) {
        String studentNum= dataRequest.getString("studentNum");
        List dataList = getEvaluationMapListByStudentNum(studentNum);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/evaluationDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public DataResponse evaluationDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Evaluation s= null;
        Optional<Evaluation> op;
        if(id != null) {
            op= evaluationRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            evaluationRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/evaluationEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse evaluationEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Evaluation sc= null;
        Student s;
        Optional<Evaluation> op;
        if(id != null) {
            op= evaluationRepository.findById(id);
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        List evaluatorIdList = new ArrayList();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            evaluatorIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        form.put("evaluatorId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("evaluatorId",sc.getEvaluator().getId());
            form.put("eval",sc.getEval());
        }
        form.put("studentIdList",studentIdList);
        form.put("evaluatorIdList",evaluatorIdList);
        Map data = new HashMap();
        data.put("form",form);
        data.put("studentIdList",studentIdList);
        data.put("evaluatorIdList",evaluatorIdList);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }


    @PostMapping("/addEvaluationInit")
    @PreAuthorize("hasRole('USER')")
    public DataResponse addEvaluationInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("id");
        String evaluatorNum = dataRequest.getString("evaluatorNum");
        Evaluation sc= new Evaluation();
        Integer id = getNewEvaluationId(); //获取鑫的主键，这个是线程同步问题;
        sc.setId(id);  //设置新的id
        sc.setStudent(studentRepository.findById(studentId).get());
        sc.setEvaluator(studentRepository.findByStudentNum(evaluatorNum).get());
        Map form = new HashMap();
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("evaluatorId",sc.getEvaluator().getId());
            form.put("eval",sc.getEval());
        }
        Map data = new HashMap();
        data.put("form",form);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewEvaluationId(){
        Integer  id = evaluationRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/evaluationEditSubmit")
    @PreAuthorize(" hasRole('ADMIN') or hasRole('USER')")
    public DataResponse evaluationEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer evaluatorId = CommonMethod.getInteger(form,"evaluatorId");
        String eval = CommonMethod.getString(form,"eval");
        Date evalTime = new Date();
        Evaluation sc= null;
        Student s= null;
        Student c = null;
        Optional<Evaluation> op;
        if(id != null) {
            op= evaluationRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Evaluation();   //不存在 创建实体对象
            id = getNewEvaluationId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setEvaluator(studentRepository.findById(evaluatorId).get());
        sc.setEval(eval);
        sc.setEvalTime(evalTime);
        evaluationRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }

    @PostMapping("/doEvaluationAdd1")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse doEvaluationAdd1(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("id");
        String evaluatorNum = dataRequest.getString("evaluatorNum");
        String eval = dataRequest.getString("eval");
        Date evalTime = new Date();
        Integer evaluation = null;
        Evaluation sc= null;
        Student s= null;
        Student c = null;
        Optional<Evaluation> op;
        Integer id;
        sc = new Evaluation();   //不存在 创建实体对象
        id = getNewEvaluationId(); //获取鑫的主键，这个是线程同步问题;
        sc.setId(id);  //设置新的id
        s = studentRepository.findByStudentNum(evaluatorNum).get();
        c = studentRepository.findById(studentId).get();
        if(Objects.equals(s.getStudentNum(), c.getStudentNum())) {
            return CommonMethod.getReturnMessageError("不能给自己评价");
        }
        sc.setStudent(c);  //设置属性
        sc.setEvaluator(s);
        sc.setEval(eval);
        sc.setEvalTime(evalTime);
        evaluationRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnMessageOK("评价成功");  //
    }

}

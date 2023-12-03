package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
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

public class UserTagController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserTagRepository userTagRepository;

    public List getAllUserTagMapList() {
        List dataList = new ArrayList();
        List<UserTag> sList = userTagRepository.findAll();  //数据库查询操作
        if(sList.size() == 0)
            return dataList;
        UserTag ut;
        Tag t;
        Student student;
        Student tagger;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            ut = sList.get(i);
            t = ut.getTag();
            student = ut.getStudent();
            tagger = ut.getTagger();
            m = new HashMap();
            m.put("id", ut.getId());
            m.put("studentName",student.getStudentName());
            m.put("taggerName",tagger.getStudentName());
            m.put("tag",t.getTag());
            dataList.add(m);
        }
        return dataList;
    }

    public List getUserTagMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<UserTag> sList = userTagRepository.findUserTagListByStudentId(studentId);  //数据库查询操作
        if(sList.size() == 0)
            return dataList;
        UserTag ut;
        Tag t;
        Student student;
        Student tagger;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            ut = sList.get(i);
            t = ut.getTag();
            student = ut.getStudent();
            tagger = ut.getTagger();
            m = new HashMap();
            m.put("id", ut.getId());
            m.put("studentName",student.getStudentName());
            m.put("taggerName",tagger.getStudentName());
            m.put("taggerNum",tagger.getStudentNum());
            m.put("tag",t.getTag());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/userTagInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse userTagInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getAllUserTagMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/userTagQueryByStudentId")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public DataResponse userTagQueryByStudentId(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        List dataList = getUserTagMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/userTagDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public DataResponse userTagDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        UserTag s= null;
        Optional<UserTag> op;
        if(id != null) {
            op= userTagRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            userTagRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    @PostMapping("/userTagEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        UserTag sc= null;
        Tag t;
        Student student;
        Student tagger;
        Optional<UserTag> op;
        if(id != null) {
            op= userTagRepository.findById(id);
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            student =sList.get(i);
            m = new HashMap();
            m.put("label",student.getStudentName());
            m.put("value",student.getId());
            studentIdList.add(m);
        }
        List taggerIdList = new ArrayList();
        List<Student> cList = studentRepository.findAll();
        for(i = 0; i <cList.size();i++) {
            tagger =cList.get(i);
            m = new HashMap();
            m.put("label",tagger.getStudentName());
            m.put("value",tagger.getId());
            taggerIdList.add(m);
        }
        List tagIdList = new ArrayList();
        List<Tag> tList = tagRepository.findAll();
        for(i = 0; i <tList.size();i++) {
            t =tList.get(i);
            m = new HashMap();
            m.put("label",t.getTag());
            m.put("value",t.getId());
            tagIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        form.put("taggerId","");
        form.put("tag", "");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("taggerId",sc.getStudent().getId());
            form.put("tagId",sc.getTag().getId());
        }
        form.put("studentIdList",studentIdList);
        form.put("taggerIdList",taggerIdList);
        form.put("tagIdList",tagIdList);

        Map data = new HashMap();
        data.put("form",form);
        data.put("studentIdList",studentIdList);
        data.put("taggerIdList",taggerIdList);
        data.put("tagIdList",tagIdList);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewUserTagId(){
        Integer  id = userTagRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/userTagEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer taggerId = CommonMethod.getInteger(form,"taggerId");
        Integer tagId = CommonMethod.getInteger(form,"tagId");
        if (studentId == null || taggerId == null || tagId == null) {
            return CommonMethod.getReturnMessageError("不能有选项为空！");
        }
        UserTag sc= null;
        Optional<UserTag> op;
        if(id != null) {
            op= userTagRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new UserTag();   //不存在 创建实体对象
            id = getNewUserTagId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setTagger(studentRepository.findById(taggerId).get());
        sc.setTag(tagRepository.findById(tagId).get());
        userTagRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }

    @PostMapping("/doUserTagAdd1")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse doUserTagAdd1(@Valid @RequestBody DataRequest dataRequest) {
        String taggerNum = dataRequest.getString("taggerNum");
        Integer studentId = dataRequest.getInteger("studentId");
        Integer tagId = dataRequest.getInteger("tagId");
//        System.out.println(taggerNum +"-"+ studentId +"-"+ tagId);
        if (userTagRepository.findByStudentTaggerTag(studentId, taggerNum, tagId).isPresent()) {
            return CommonMethod.getReturnMessageError("您已经添加过此标签了");
        }
        Integer score = null;
        UserTag sc= null;
        Student student= null;
        Student tagger = null;
        Tag t = null;
        Optional<UserTag> op;
        Integer id;
        sc = new UserTag();   //不存在 创建实体对象
        id = getNewUserTagId(); //获取鑫的主键，这个是线程同步问题;
        sc.setId(id);  //设置新的id
        tagger = studentRepository.findByStudentNum(taggerNum).get();
        student = studentRepository.findById(studentId).get();
        t = tagRepository.getById(tagId);
        sc.setStudent(student);  //设置属性
        sc.setTagger(tagger);
        sc.setTag(t);
        userTagRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnMessageOK("添加成功");  //
    }

}

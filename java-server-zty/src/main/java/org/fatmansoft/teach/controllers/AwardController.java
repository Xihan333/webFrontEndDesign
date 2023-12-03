package org.fatmansoft.teach.controllers;


import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.models.Award;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.AwardRepository;
import org.fatmansoft.teach.repository.StudentRepository;
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

public class AwardController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用AwardRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， awardRepository 相当于AwardRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用

    @Autowired
    private AwardRepository awardRepository;
    @Autowired
    private StudentRepository studentRepository;

    //getAwardMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("awardNum",s.getAwardNum())， awardNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getAwardMapList(String name) {
        List dataList = new ArrayList();
        List<Award> sList = awardRepository.findAwardListByName(name);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Award s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("awardName",s.getAwardName());
            m.put("student",s.getStudent().getStudentName());
            m.put("studentNum", s.getStudent().getStudentNum());
            m.put("info",s.getInfo());
            m.put("awardTime", DateTimeTool.parseDateTime(s.getAwardTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    public List getAwardMapListForStudent(String name, String studentNum) {
        List dataList = new ArrayList();
        List<Award> sList = awardRepository.findAwardListByNameForStudent(name, studentNum);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Award s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("awardName",s.getAwardName());
            m.put("student",s.getStudent().getStudentName());
            m.put("studentNum", s.getStudent().getStudentNum());
            m.put("info",s.getInfo());
            m.put("awardTime", DateTimeTool.parseDateTime(s.getAwardTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }

    public List getAwardMapList2(Integer studentId) {
        List dataList = new ArrayList();
        List<Award> sList = awardRepository.findAwardListByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Award s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("awardName",s.getAwardName());
            m.put("student",s.getStudent().getStudentName());
            m.put("studentNum", s.getStudent().getStudentNum());
            m.put("info",s.getInfo());
            m.put("awardTime", DateTimeTool.parseDateTime(s.getAwardTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    //award页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getAwardMapList，返回所有学生数据，
    @PostMapping("/awardInit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse awardInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getAwardMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/awardInit2")
    @PreAuthorize("hasRole('USER')")
    public DataResponse awardInit2(@Valid @RequestBody DataRequest dataRequest) {
        String studentNum =dataRequest.getString("studentNum");
        Integer studentId =  studentRepository.findByStudentNum(studentNum).get().getId();
        List dataList = getAwardMapList2(studentId);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    //award页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getAwardMapList，返回所有学生数据，
    @PostMapping("/awardQuery")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse awardQuery(@Valid @RequestBody DataRequest dataRequest) {
        String name= dataRequest.getString("name");
        List dataList = getAwardMapList(name);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/awardQueryForStudent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse awardQueryForStudent(@Valid @RequestBody DataRequest dataRequest) {
        String name= dataRequest.getString("name");
        String studentNum = dataRequest.getString("studentNum");
        List dataList = getAwardMapListForStudent(name, studentNum);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Award页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/awardDelete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse awardDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Award s= null;
        Optional<Award> op;
        if(id != null) {
            op= awardRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            awardRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //awardEdit初始化方法
    //awardEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/awardEditInit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse awardEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Award s= null;
        Optional<Award> op;
        if(id != null) {
            op= awardRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }

        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("awardName",s.getAwardName());
            form.put("studentNum",s.getStudent().getStudentNum());
            form.put("info",s.getInfo());
            form.put("awardTime", DateTimeTool.parseDateTime(s.getAwardTime(),"yyyy-MM-dd"));  //时间格式转换字符串
        }
        Map data = new HashMap();
        data.put("form",form);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Award 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewAwardId(){
        Integer
                id = awardRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/awardEditSubmit")
    @PreAuthorize(" hasRole('USER') or hasRole('ADMIN')")
    public DataResponse awardEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String awardName = CommonMethod.getString(form,"awardName");
        String studentNum = CommonMethod.getString(form, "studentNum");
        String info = CommonMethod.getString(form, "info");
        Date awardTime = CommonMethod.getDate(form,"awardTime");

        if (Objects.equals(awardName, ""))
            return CommonMethod.getReturnMessageError("奖励名称不能为空");
        Student t;
        if (studentNum == null) {
            String username = dataRequest.getString("username");
            Optional<Student> ot = studentRepository.findByStudentNum(username);
            t = ot.get();
        } else {
            if (studentRepository.findByStudentNum(studentNum).isEmpty())
                return CommonMethod.getReturnMessageError("此学生不存在");
            t = studentRepository.findByStudentNum(studentNum).get();
        }

        Award s= null;
        Optional<Award> op;
        if(id != null) {
            op= awardRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Award();   //不存在 创建实体对象
            id = getNewAwardId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        //设置属性
        s.setAwardName(awardName);
        s.setStudent(t);
        s.setInfo(info);
        s.setAwardTime(awardTime);
        awardRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

}

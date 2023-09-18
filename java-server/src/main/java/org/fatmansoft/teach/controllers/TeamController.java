package org.fatmansoft.teach.controllers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.*;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Value("${attach.folder}")    //环境配置变量获取
    private String attachFolder;  //服务器端数据存储
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private UserTypeRepository userTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeamWorkRepository teamWorkRepository;
    @Autowired
    private TaskStudentRepository taskStudentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @PostMapping(path = "/importStudentData")
    public DataResponse importStudentData(@RequestBody byte[] barr,
                                          @RequestParam(name = "uploader") String uploader,
                                          @RequestParam(name = "fileName") String fileName) {
        try {
            InputStream in = new ByteArrayInputStream(barr);
            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            Cell cell;
            int i;
            i = 1;
            String num, name, className,groupNo;
            Integer personId;
            Person p;
            Student s;
            User u;
            String password = encoder.encode("123456");
            String major="软件工程";
            String dept ="软件学院";
            rowIterator.next();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                cell = row.getCell(0);
                if (cell == null)
                    break;
                num = cell.getStringCellValue();  //获取一行消费记录 日期 金额
                cell = row.getCell(1);
                name = cell.getStringCellValue();
                cell = row.getCell(2);
                className = cell.getStringCellValue();
                cell = row.getCell(3);
                groupNo = cell.getStringCellValue();
                p = new Person();
                p.setNum(num);
                p.setName(name);
                p.setDept(dept);
                p.setType("1");
                personRepository.saveAndFlush(p);  //插入新的Person记录
                u= new User();
                u.setPerson(p);
                u.setUserName(num);
                u.setPassword(password);
                u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
                userRepository.saveAndFlush(u); //插入新的User记录
                s = new Student();   // 创建实体对象
                s.setPerson(p);
                s.setMajor(major);
                s.setClassName(className);
//                if(groupNo != null && groupNo.length() > 0)
//                    s.setGroupNo(groupNo);
                studentRepository.saveAndFlush(s);  //插入新的Student记录
                System.out.println(i++);
            }
            workbook.close();  //关闭Excl输入流
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("导入错误！");
        }
    }
    public Integer getCurrentTaskId(DataRequest dataRequest) {
        Integer currentTaskId = dataRequest.getInteger("currentTaskId");
        if(currentTaskId== null || currentTaskId== 0)
            currentTaskId = ComDataUtil.getInstance().currentTaskId();
        return currentTaskId;
    }
    @PostMapping("/getStudentTeamInfo")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentTeamInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer taskStudentId = dataRequest.getInteger("taskStudentId");
        Optional<TaskStudent> tsOp;
        TaskStudent ts = null;
        if(taskStudentId != null) {
            tsOp = taskStudentRepository.findById(taskStudentId);
        }else {
            tsOp = taskStudentRepository.findByStudentPersonNumAndTaskTaskId(CommonMethod.getUsername(),getCurrentTaskId(dataRequest));
        }
        if(!tsOp.isPresent())
            return CommonMethod.getReturnMessageError("选课不存在！");
        ts = tsOp.get();
        Student s= ts.getStudent();
        Map data = new HashMap();
        Map info = studentService.getMapFromStudent(s);  // 查询学生信息Map对象
        info.put("taskStudentId",ts.getTaskStudentId());
        info.put("teachNo",ts.getTeachNo());
        if(ts.getTeam() != null) {
            Team t = ts.getTeam();
            info.put("teamNo", t.getNum());
            info.put("teamNoName", "团队" + t.getNum());
            if (t.getLaboratory() != null) {
                info.put("laboratory", t.getLaboratory());
                info.put("laboratoryName", ComDataUtil.getInstance().getDictionaryLabelByValue("SYSM", t.getLaboratory())); //性别类型的值转换成数据类型名
            }
            if (ts.getScore() != null)
                info.put("teamScore", ts.getScore());
        }
        if(ts.getWeight() != null)
            info.put("weight",ts.getWeight());
        if(ts.getScore() != null)
            info.put("score",ts.getScore());
        if(ts.getLevel() != null)
            info.put("level",ts.getLevel());
        List teamMemberList = new ArrayList();
        Map m;
        info.put("teamId",0);
        Team t= ts.getTeam();
        if(t != null ) {
            List<TaskStudent> gList = taskStudentRepository.findByTeamTeamId(t.getTeamId());
            for(TaskStudent gs : gList) {
                m = new HashMap();
                s = gs.getStudent();
                m.put("taskStudentId", gs.getTaskStudentId());
                m.put("studentId",s.getStudentId());
                m.put("num", s.getPerson().getNum());
                m.put("name", s.getPerson().getName());
                m.put("className", s.getClassName());
                m.put("teachNo",gs.getTeachNo());
                m.put("weight", gs.getWeight());
                m.put("score", gs.getScore());
                m.put("level", gs.getLevel());
                teamMemberList.add(m);
            }
            if(t.getUploader() != null)
                info.put("uploader",t.getUploader().getPerson().getNum()+"-"+t.getUploader().getPerson().getName());
            info.put("uploadTime",t.getUploadTime());
            info.put("teamId",t.getTeamId());
        }
        ComDataUtil pi = ComDataUtil.getInstance();
        data.put("teamCanApply", pi.teamCanApply());
        data.put("teamLock", pi.teamLock());
        data.put("teamView", pi.teamView());
        data.put("info",info);
        data.put("teamMemberList",teamMemberList);
        return CommonMethod.getReturnData(data);//将前端所需数据保留Map对象里，返还前端
    }
    @PostMapping("/saveStudentTeamInfo")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse saveStudentTeamInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer taskStudentId = dataRequest.getInteger("taskStudentId");
        String weightStr = dataRequest.getString("weight");
        String laboratory = dataRequest.getString("laboratory");
        double weight = 1;
        if(weightStr != null && weightStr.length() > 0) {
            weight = Double.parseDouble(weightStr);
        }
        Optional<TaskStudent> sOp= taskStudentRepository.findById(taskStudentId);
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        TaskStudent s= sOp.get();
        s.setWeight(weight);
        taskStudentRepository.save(s);
        if(laboratory != null && laboratory.length() != 0 && s.getTeam() != null) {
            Team t = s.getTeam();
            t.setLaboratory(laboratory);
            teamRepository.save(t);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public List getTeamStudentList(List <Team> tList, List<TaskStudent>sList){
        List dataList = new ArrayList();
        Map<Integer, Map> tMap = new HashMap();
        Map m;
        String groupNo;
        TaskStudent ts;
        Student s;
        Person p;
        int count;
        Team t;
        int i;
        String studentStr;
        Integer teamId;
        double teamWeight;
        if(tList == null || tList.size() == 0)
            return dataList;
        for(i = 0; i < tList.size();i++) {
            t = tList.get(i);
            m = new HashMap();
            m.put("select",false);
            m.put("teamId",t.getTeamId());
            m.put("teamNo",t.getNum());
            m.put("laboratory",t.getLaboratory());
            m.put("count",0);
            m.put("teamWeight",0d);
            m.put("teamScore",t.getScore());
            if(t.getUploader() != null) {
                m.put("uploader", t.getUploader().getNumName());
                m.put("uploadTime",t.getUploadTime());
            }else {
                m.put("uploader", "");
                m.put("uploadTime","");
            }
            tMap.put(t.getTeamId(),m);
            dataList.add(m);
        }
        if(sList == null || sList.size() == 0 )
            return dataList;
        for(i = 0; i < sList.size();i++) {
            ts = sList.get(i);
            s = ts.getStudent();
            p = s.getPerson();
            t = ts.getTeam();
            if(t == null)
                continue;
            teamId = t.getTeamId();
            m = tMap.get(teamId);
            if(m == null)
                continue;
            count = (int)m.get("count");
            teamWeight= (double)m.get("teamWeight");
            count++;
            teamWeight += ts.getWeight();
            studentStr = s.getNumName();
            if(ts.getScore() != null)
                studentStr += ":" + ts.getScore();
            if(ts.getLevel() != null)
                studentStr += "-" + ts.getLevel();
            m.put("taskStudentId"+ count,ts.getTaskStudentId());
            m.put("student"+count,studentStr);
            m.put("weight"+count,ts.getWeight()+"");
            m.put("count",count);
            m.put("teamWeight",teamWeight);
        }
        return dataList;
    }
    @PostMapping("/getTeamManageData")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeamManageData(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        Student s;
        List<Team> tList= teamRepository.findByTaskId(currentTaskId);
        List<TaskStudent> sList = taskStudentRepository.findByTaskTaskId(currentTaskId);
        List dataList = getTeamStudentList(tList,sList);
        List<OptionItem> teamList = new ArrayList();
        for (Team t : tList) {
            teamList.add(new OptionItem(t.getTeamId(), t.getNum(), t.getNum()));
        }

        List<OptionItem> studentList = new ArrayList();
        for (TaskStudent ts : sList) {
            s = ts.getStudent();
            studentList.add(new OptionItem(ts.getTaskStudentId(), s.getPerson().getNum(), s.getNumName()));
        }
        Map data = new HashMap();
        data.put("currentTaskId",currentTaskId);
        data.put("dataList", dataList);
        data.put("teamList", teamList);
        data.put("studentList", studentList);
        return CommonMethod.getReturnData(data);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeamDataList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeamDataList(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        if(currentTaskId != null) {
            currentTaskId = ComDataUtil.getInstance().currentTaskId();
        }
        Student s;
        List<Team> tList= teamRepository.findByTaskId(currentTaskId);
        List<TaskStudent> sList = taskStudentRepository.findByTaskTaskId(currentTaskId);
        List dataList = getTeamStudentList(tList,sList);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getTeamStudentListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getTeamStudentListExcl(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        Student s;
        List<Team> tList= teamRepository.findByTaskId(currentTaskId);
        List<TaskStudent> sList = taskStudentRepository.findByTaskTaskId(currentTaskId);
        List list = getTeamStudentList(tList,sList);
        Integer widths[] = {8, 20, 40, 40, 40, 40, 40,20,20};
        int i, j, k;
        String titles[] = {"序号","团队编号", "成员一", "成员二", "成员三", "成员四", "成员五", "总权重", "团队成绩"};
        String outPutSheetName = "student.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for(j=0;j < widths.length;j++) {
            sheet.setColumnWidth(j,widths[j]*256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell cell[] = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map m;
        int count;
        String str;
        if (list != null && list.size() > 0) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = (Map) list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m,"teamNo"));
                count = CommonMethod.getInteger(m,"count");
                for(k = 1;k<=count;k++) {
                    cell[1+k].setCellValue(CommonMethod.getString(m,"student"+k)+"("+CommonMethod.getInteger(m,"weight"+k)+")");
                }
                cell[7].setCellValue(CommonMethod.getString(m,"teamWeight"));
                cell[8].setCellValue(CommonMethod.getString(m,"teamScore"));
            }
        }
        try {
            StreamingResponseBody stream = outputStream -> {
                wb.write(outputStream);
            };
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    public String getNewTeamNo(){
        String newStr;
        String max = teamRepository.getMaxNum();
        if(max == null || max.length() == 0) {
            newStr = ComDataUtil.getInstance().getTeamNumPrefix()+"01";
        }else
            newStr = CommonMethod.getNextNum2(max);
        return newStr;
    }
    @PostMapping("/addNewTeam")
    public DataResponse addNewTeam(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        Integer taskStudentId = dataRequest.getInteger("taskStudentId");
        Person p= null;
        Student s = null;
        TaskStudent ts= null;
        if(taskStudentId != null) {
            Optional<TaskStudent> sOp = taskStudentRepository.findById(taskStudentId);
            if(sOp.isPresent()) {
                ts= sOp.get();
                s = ts.getStudent();
                p = s.getPerson();
            }
        }
        if(p == null) {
            Optional<User> uOp = userRepository.findByUserId(CommonMethod.getUserId());
            if (uOp.isPresent()) {
                p = uOp.get().getPerson();
            }
        }
        String newStr = getNewTeamNo();
        Team t = new Team();
        t.setTaskId(currentTaskId);
        t.setNum(newStr);
        t.setCreator(p);
        t.setCreateTime(DateTimeTool.parseDateTime(new Date(),"yyyy-MM-dd HH:mm:ss"));
        teamRepository.save(t);
        if(ts != null) {
            ts.setTeam(t);
            taskStudentRepository.save(ts);
        }
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/setStudentTeam")
    public DataResponse setStudentTeam(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        Integer taskStudentId = dataRequest.getInteger("taskStudentId");
        Integer teamId = dataRequest.getInteger("teamId");
        Team t= null;
        if(teamId != null && teamId > 0) {
            Optional<Team> tOp = teamRepository.findById(teamId);
            if (tOp.isPresent())
                t = tOp.get();
        }
        Optional<TaskStudent> sOp= taskStudentRepository.findById(taskStudentId);
        if(!sOp.isPresent()) {
            return CommonMethod.getReturnMessageOK();
        }
        TaskStudent s = sOp.get();
        s.setTeam(t);
        taskStudentRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/modifyTeamInfo")
    public DataResponse modifyTeamData(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        List dataList = (List) dataRequest.get("dataList");
        if(dataList == null || dataList.size() == 0)
            return CommonMethod.getReturnMessageOK();
        int i,k;
        Map m;
        Integer teamId;
        Boolean b;
        Team t;
        Optional<Team> tOp;
        Optional<TaskStudent> sOp;
        Integer studentId;
        Double weight;
        TaskStudent s;
        boolean teamModify = false;
        for(i = 0; i < dataList.size();i++) {
            teamModify = false;
            t = null;
            m = (Map)dataList.get(i);
            teamId = CommonMethod.getInteger(m,"teamId");
            b= (Boolean)m.get("ml");
            if(b!= null && b.booleanValue()) {
                tOp = teamRepository.findById(teamId);
                if(tOp.isPresent()) {
                    t = tOp.get();
                }
                if(t != null) {
                    t.setLaboratory(CommonMethod.getString(m, "laboratory"));
                    teamModify= true;
                }
            }
            b= (Boolean)m.get("ms");
            if(b!= null && b.booleanValue()) {
                if(t == null) {
                    tOp = teamRepository.findById(teamId);
                    if(tOp.isPresent()) {
                        t = tOp.get();
                    }
                }
                if(t != null) {
                    t.setScore(CommonMethod.getDouble(m, "teamScore"));
                    teamModify = true;
                }
            }
            if(teamModify) {
                teamRepository.save(t);
            }
            for(k = 1;k<=5;k++) {
                b = (Boolean) m.get("m"+k);
                if(b == null || !b.booleanValue())
                    continue;
                studentId = CommonMethod.getInteger(m,"studentId"+k);
                weight = CommonMethod.getDouble(m,"weight"+k);
                sOp = taskStudentRepository.findByStudentStudentIdAndTaskTaskId(studentId,currentTaskId);
                if(sOp.isPresent()) {
                    s = sOp.get();
                    s.setWeight(weight);
                    taskStudentRepository.save(s);
                }
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping(path = "/uploadTeamFile")
    public DataResponse uploadTeamFile(@RequestBody byte[] barr,
                                   @RequestParam(name = "uploader") String uploader,
                                   @RequestParam(name = "remoteFile") String remoteFile,
                                   @RequestParam(name = "fileName") String fileName)  {
        try {
            OutputStream os = new FileOutputStream(new File(attachFolder + remoteFile));
            os.write(barr);
            os.close();
            Optional<TaskStudent> sOp = taskStudentRepository.findByStudentPersonNumAndTaskTaskId(CommonMethod.getUsername(),ComDataUtil.getInstance().currentTaskId());
            if(sOp.isPresent()) {
                Team t = sOp.get().getTeam();
                t.setUploader(sOp.get().getStudent());
                t.setUploadTime(DateTimeTool.parseDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
                teamRepository.save(t);
            }
            return CommonMethod.getReturnMessageOK();
        }catch(Exception e){
            return CommonMethod.getReturnMessageError("上传错误");
        }
    }


    @PostMapping("/saveWorkContent")
    public DataResponse saveWorkContent(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = (List) dataRequest.get("dataList");
        if(dataList == null || dataList.size() == 0)
            return CommonMethod.getReturnMessageOK();
        int i;
        Map m;
        Integer workId;
        Optional<TeamWork> tOp;
        String content;
        TeamWork w;
        String nowStr = DateTimeTool.parseDateTime(new Date(),"yyyy-MM-dd HH:mm:ss");
        for(i = 0; i < dataList.size();i++) {
            m = (Map)dataList.get(i);
            workId = CommonMethod.getInteger(m,"workId");
            content = CommonMethod.getString(m,"content");
            tOp = teamWorkRepository.findById(workId);
            if(tOp.isPresent()) {
                w = tOp.get();
                w.setContent(content);
                w.setModifyTime(nowStr);
                teamWorkRepository.save(w);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping(value = "/importStudentExcl", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public DataResponse importStudentExcl(@RequestParam Map pars, @RequestParam("file") MultipartFile file) {
        try{
            Integer currentTaskId = CommonMethod.getInteger(pars,"currentTaskId" );
            Task task =taskRepository.findById(currentTaskId).get();
            InputStream in = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            Cell cell;
            int i;
            i = 1;
            String num, name, className;
            Optional<TaskStudent> tsOp;
            TaskStudent ts;
            Optional<Student> sOp;
            Student s;
            Person p;
            User u;
            String password = encoder.encode("123456");
            String major="软件工程";
            String dept ="软件学院";
            String teachNo;
            rowIterator.next();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                cell = row.getCell(0);
                if (cell == null )
                    break;
                num = cell.getStringCellValue();  //获取一行消费记录 日期 金额
                if(num == null || num.length()==0)
                    continue;
                cell = row.getCell(1);
                name = cell.getStringCellValue();
                cell = row.getCell(2);
                className = cell.getStringCellValue();
                cell = row.getCell(3);
                teachNo = cell.getStringCellValue();
                tsOp = taskStudentRepository.findByStudentPersonNumAndTaskTaskId(num,currentTaskId);
                if(tsOp.isPresent()) {
                    continue;
                }
                sOp = studentRepository.findByPersonNum(num);
                if(!sOp.isPresent()) {
                    p = new Person();
                    p.setNum(num);
                    p.setName(name);
                    p.setDept(dept);
                    p.setType("1");
                    personRepository.saveAndFlush(p);  //插入新的Person记录
                    u = new User();
                    u.setPerson(p);
                    u.setUserName(num);
                    u.setPassword(password);
                    u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
                    userRepository.saveAndFlush(u); //插入新的User记录
                    s = new Student();   // 创建实体对象
                    s.setPerson(p);
                    s.setMajor(major);
                    s.setClassName(className);
                    studentRepository.saveAndFlush(s);  //插入新的Student记录
                }else {
                    s = sOp.get();
                }
                ts = new TaskStudent();
                ts.setStudent(s);
                ts.setTask(task);
                ts.setTeachNo(teachNo);
                ts.setWeight(1d);
                taskStudentRepository.save(ts);
                System.out.println(i++);
            }
            workbook.close();  //关闭Excl输入流
            return CommonMethod.getReturnMessageOK();
        }catch(Exception e){

        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping(value = "/uploadTeamFileWeb", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public DataResponse uploadTeamFileWeb(@RequestParam Map pars, @RequestParam("file") MultipartFile file) {
        try{
            Integer taskStudentId = CommonMethod.getInteger(pars,"taskStudentId" );

            String remoteFile = CommonMethod.getString(pars,"remoteFile");
            InputStream in = file.getInputStream();
            int size = (int)file.getSize();
            byte [] data = new byte[size];
            in.read(data);
            in.close();
            OutputStream os = new FileOutputStream(new File(attachFolder + ComDataUtil.getInstance().currentPath()+"/"+remoteFile));
            os.write(data);
            os.close();
            Optional<TaskStudent> sOp = taskStudentRepository.findById(taskStudentId);
            if(sOp.isPresent()) {
                Team t = sOp.get().getTeam();
                t.setUploader(sOp.get().getStudent());;
                t.setUploadTime(DateTimeTool.parseDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
                teamRepository.save(t);
            }
            return CommonMethod.getReturnMessageOK();
        }catch(Exception e){

        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/getTeamProjectFileWeb")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') ")
    public ResponseEntity<StreamingResponseBody> getTeamProjectFileWeb(@Valid @RequestBody DataRequest dataRequest) {
        try {
            Integer currentTaskId = getCurrentTaskId(dataRequest);
            String teamNo = dataRequest.getString("teamNo");
            if(teamNo == null || teamNo.length()==0) {
                Optional<TaskStudent> sOp = taskStudentRepository.findByStudentPersonNumAndTaskTaskId(CommonMethod.getUsername(),currentTaskId);
                if(sOp.isPresent()) {
                    Team t = sOp.get().getTeam();
                    teamNo = t.getNum();
                }
            }
            if(teamNo != null) {
                File file = new File(attachFolder +ComDataUtil.getInstance().currentPath()+ "/project/"+teamNo + ".zip");
                if (file.exists()) {
                    int size = (int) file.length();
                    byte data[] = new byte[size];
                    FileInputStream in = new FileInputStream(file);
                    in.read(data);
                    in.close();
                    MediaType mType = null;
                    StreamingResponseBody stream = outputStream -> {
                        outputStream.write(data);
                    };
                    return ResponseEntity.ok()
                            .contentType(mType)
                            .body(stream);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/getTaskOptionItemList")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') ")
    public DataResponse getTaskOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        List<Task> tList= taskRepository.findAll();
        Map<Integer, OptionItem> map = new HashMap<>();
        List<OptionItem> teamList = new ArrayList();
        OptionItem item;
        String title;
        Integer taskId;
        Object a[];
        Course c ;
        for (Task t : tList) {
            c = t.getCourse();
            item = new OptionItem();
            taskId = t.getTaskId();
            item.setId(taskId);
            item.setValue(c.getNum());
            title = c.getNum() + "-" + c.getName() + "("+ t.getTaskNo()+")";
            item.setTitle(title);
            teamList.add(item);
        }
        return CommonMethod.getReturnData(teamList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getTeamOptionItemList")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') ")
    public DataResponse getTeamOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        List<Team> tList= teamRepository.getSortedTeamList(currentTaskId);
        Map<Integer, OptionItem> map = new HashMap<>();
        List<OptionItem> teamList = new ArrayList();
        OptionItem item;
        String title;
        Integer teamId;
        Object a[];
        for (Team t : tList) {
            item = new OptionItem();
            teamId = t.getTeamId();
            item.setId(teamId);
            item.setValue(t.getNum());
            title = t.getNum() + ":";
            item.setTitle(title);
            teamList.add(item);
            map.put(teamId,item);
        }
        List sList = taskStudentRepository.findTeamMemberNameList(currentTaskId);
        for(int i = 0; i < sList.size();i++) {
            a= (Object[])sList.get(i);
            teamId = (Integer)a[0];
            item = map.get(teamId);
            if(item.getTitle().endsWith(":"))
                item.setTitle(item.getTitle()+a[1]);
            else
                item.setTitle(item.getTitle()+";"+a[1]);
        }
        return CommonMethod.getReturnData(teamList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/saveTeamScore")
    @PreAuthorize(" hasRole('ADMIN') ")
    public DataResponse saveTeamScore(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        Integer type= dataRequest.getInteger("type");
        List dataList = (List) dataRequest.get("dataList");
        if(dataList == null || dataList.size() == 0)
            return CommonMethod.getReturnMessageOK();
        int i,k;
        Map m;
        Integer teamId;
        Boolean b;
        Team t;
        Optional<Team> tOp;
        Optional<TaskStudent> sOp;
        Integer taskStudentId;
        Double weight;
        TaskStudent s;
        for(i = 0; i < dataList.size();i++) {
            m = (Map)dataList.get(i);
            teamId = CommonMethod.getInteger(m,"teamId");
            tOp = teamRepository.findById(teamId);
            if(!tOp.isPresent()) {
                continue;
            }
            t = tOp.get();
            switch(type) {
                case 1:
                    for(k = 1;k<=5;k++) {
                        taskStudentId = CommonMethod.getInteger(m,"taskStudentId"+k);
                        if(taskStudentId ==  null)
                            break;
                        weight = CommonMethod.getDouble(m,"weight"+k);
                        sOp = taskStudentRepository.findById(taskStudentId);
                        if(sOp.isPresent()) {
                            s = sOp.get();
                            s.setWeight(weight);
                            taskStudentRepository.save(s);
                        }
                    }
                    break;
                case 2:
                    t.setScore(CommonMethod.getDouble(m,"teamScore"));
                    teamRepository.save(t);
                    break;
                case 3:
                    Double wight = taskStudentRepository.getSumWight(teamId);
                    if(wight== null)
                        break;
                    List<TaskStudent> sList = taskStudentRepository.findByTeamTeamId(teamId);
                    int count= sList.size();
                    double score = t.getScore();
                    double sc;
                    for(k = 0; k < sList.size();k++) {
                        s = sList.get(k);
                        sc = s.getWeight()*count*score/wight;
                        s.setScore(CommonMethod.getDouble2(sc));
                        s.setLevel(CommonMethod.getLevelFromScore(sc));
                        taskStudentRepository.save(s);
                    }
                    break;
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/getTaskStudentDataList")
    @PreAuthorize(" hasRole('ADMIN') ")
    public DataResponse getTaskStudentDataList(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        List<TaskStudent> tList= taskStudentRepository.findByTaskTaskId(currentTaskId);
        Map m;
        Team t;
        Student s;
        Person p;
        List dataList = new ArrayList();
        for (TaskStudent ts : tList) {
            s = ts.getStudent();
            p = s.getPerson();
            t = ts.getTeam();
            m = new HashMap();
            m.put("taskStudentId",ts.getTaskStudentId());
            m.put("num", p.getNum());
            m.put("name", p.getName());
            m.put("className", s.getClassName());
            m.put("teachNo", ts.getTeachNo());
            if(t != null) {
                m.put("teamNo", t.getNum());
                m.put("teamScore", t.getScore());
            }
            m.put("weight", ts.getWeight());
            m.put("score", ts.getScore());
            m.put("level", ts.getLevel());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/getStudentScoreListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getStudentScoreListExcl(@Valid @RequestBody DataRequest dataRequest) {
        Integer currentTaskId = getCurrentTaskId(dataRequest);
        List<TaskStudent> list = taskStudentRepository.getStudentScoreList(currentTaskId);  //数据库查询操作
        Integer widths[] = {8, 15, 15, 15, 15, 15, 15, 15,15,9};
        int i, j, k;
        String titles[] = {"序号","学号", "姓名", "班级", "课序号", "团队", "团队成绩", "权重","成绩", "级别"};
        String outPutSheetName = "score.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for(j=0;j < widths.length;j++) {
            sheet.setColumnWidth(j,widths[j]*256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell cell[] = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map m;
        TaskStudent ts;
        Student s;
        Person p;
        Team t;
        if (list != null && list.size() > 0) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                ts = (TaskStudent)list.get(i);
                t = ts.getTeam();
                s = ts.getStudent();
                p = s.getPerson();
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(p.getNum());
                cell[2].setCellValue(p.getName());
                if(s.getClassName() != null)
                cell[3].setCellValue(s.getClassName());
                if(ts.getTeachNo() != null)
                cell[4].setCellValue(ts.getTeachNo());
                if( t != null) {
                    cell[5].setCellValue(t.getNum());
                    if(t.getScore() != null)
                        cell[6].setCellValue(t.getScore());
                }
                if(ts.getWeight() != null)
                    cell[7].setCellValue(ts.getWeight());
                if(ts.getScore() != null)
                    cell[8].setCellValue(ts.getScore());
                if(ts.getLevel() != null)
                    cell[9].setCellValue(ts.getLevel());
            }
        }
        try {
            StreamingResponseBody stream = outputStream -> {
                wb.write(outputStream);
            };
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }


    @PostMapping("/deleteTaskStudent")
    public DataResponse deleteTaskStudent(@Valid @RequestBody DataRequest dataRequest) {
        Integer taskStudentId = dataRequest.getInteger("taskStudentId");
        Optional<TaskStudent> tOp;
        if(taskStudentId == null)
            return CommonMethod.getReturnMessageError(" 主键为空，不能删除");
        tOp = taskStudentRepository.findById(taskStudentId);
        if(tOp.isPresent()) {
            taskStudentRepository.delete(tOp.get());
        }
        return CommonMethod.getReturnMessageOK();
    }

}


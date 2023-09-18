package org.fatmansoft.teach.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.StudentService;
import org.fatmansoft.teach.service.SystemService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

/**
 * StudentController 主要是为学生管理数据管理提供的Web请求服务
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student")

public class StudentController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private ScoreRepository scoreRepository;  //成绩数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //消费数据操作自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private TeamRepository termRepository;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private SystemService systemService;

    /**
     * getMapFromStudent 将学生表属性数据转换复制MAp集合里
     * @param
     * @return
     */

    /**
     * getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     *
     * @param numName 输入参数
     * @return Map List 集合
     */
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(studentService.getMapFromStudent(sList.get(i)));
        }
        return dataList;
    }

    /**
     * getStudentList 学生管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     * @return
     */


    @PostMapping("/getStudentList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    /**
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     *
     * @param dataRequest 前端studentId 药删除的学生的主键 student_id
     * @return 正常操作
     */

    @PostMapping("/studentDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");  //获取student_id值
        studentId = null;
        Student s = null;
        Optional<Student> op;
        if (studentId != null) {
            op = studentRepository.findById(studentId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该学生的账户
            if (uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该学生的账户
            }
            Person p = s.getPerson();
            studentRepository.delete(s);    //首先数据库永久删除学生信息
            personRepository.delete(p);   // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return 根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getStudentInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Student s = null;
        Optional<Student> op;
        if (studentId != null) {
            op = studentRepository.findById(studentId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(studentService.getMapFromStudent(s)); //这里回传包含学生信息的Map对象
    }

    /**
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * studentId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     *
     * @return 新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/studentEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Student s = null;
        Person p;
        User u;
        Optional<Student> op;
        Integer personId;
        boolean isNew = false;
        if (studentId != null) {
            op = studentRepository.findById(studentId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if (s == null) {
            p = new Person();
            p.setNum(num);
            p.setType("1");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            String password = encoder.encode("123456");
            u = new User();
            u.setPerson(p);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getUserId());
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Student();   // 创建实体对象
            s.setPerson(p);
            studentRepository.saveAndFlush(s);  //插入新的Student记录
            isNew = true;
        } else {
            p = s.getPerson();
            isNew = false;
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setMajor(CommonMethod.getString(form, "major"));
        s.setClassName(CommonMethod.getString(form, "className"));
        studentRepository.save(s);  //修改保存学生信息
        systemService.modifyLog(s,isNew);
        return CommonMethod.getReturnData(s.getStudentId());  // 将studentId返回前端
    }


    /**
     * getStudentScoreList 将Score对象列表集合转换成Score Map对象列表集合
     *
     * @param sList
     * @return
     */
    public List getStudentScoreList(List<Score> sList) {
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for (Score s : sList) {
            m = new HashMap();
            c = s.getCourse();
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("scoreId", s.getScoreId());
            m.put("courseNum", c.getNum());
            m.put("courseName", c.getName());
            m.put("credit", c.getCredit());
            m.put("mark", s.getMark());
            m.put("ranking", s.getRanking());
            list.add(m);
        }
        return list;
    }

    /**
     * getStudentMarkList 计算学生的的成绩等级
     *
     * @param sList 学生成绩列表
     * @return 成绩等级Map对象列表
     */
    public List getStudentMarkList(List<Score> sList) {
        String title[] = {"优", "良", "中", "及格", "不及格"};
        int count[] = new int[5];
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for (Score s : sList) {
            c = s.getCourse();
            if (s.getMark() >= 90)
                count[0]++;
            else if (s.getMark() >= 80)
                count[1]++;
            else if (s.getMark() >= 70)
                count[2]++;
            else if (s.getMark() >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for (int i = 0; i < 5; i++) {
            m = new HashMap();
            m.put("name", title[i]);
            m.put("title", title[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }

    /**
     * getStudentFeeList 获取学生的消费Map对象列表集合
     *
     * @param studentId
     * @return
     */
    public List getStudentFeeList(Integer studentId) {
        List<Fee> sList = feeRepository.findListByStudent(studentId);  // 查询某个学生消费记录集合
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for (Fee s : sList) {
            m = new HashMap();
            m.put("title", s.getDay());
            m.put("value", s.getMoney());
            list.add(m);
        }
        return list;
    }

    /**
     * getStudentIntroduceData 前端获取学生个人简历数据请求服务
     *
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return 根据studentId从数据库中查出相关数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getStudentIntroduceData")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        String username = CommonMethod.getUsername();
        Optional<Student> sOp = studentRepository.findByPersonNum(username);  // 查询获得 Student对象
        if (!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s = sOp.get();
        Map info = studentService.getMapFromStudent(s);  // 查询学生信息Map对象
        List<Score> sList = scoreRepository.findByStudentStudentId(s.getStudentId()); //获得学生成绩对象集合
        Map data = new HashMap();
        data.put("info", info);
        data.put("scoreList", getStudentScoreList(sList));
        data.put("markList", getStudentMarkList(sList));
        data.put("feeList", getStudentFeeList(s.getStudentId()));
        return CommonMethod.getReturnData(data);//将前端所需数据保留Map对象里，返还前端
    }

    /**
     * saveStudentIntroduce 前端学生个人简介信息introduce提交服务
     *
     * @param dataRequest 从前端获取 studentId student表 student_id introduce 学生个人简介信息
     * @return 操作正常
     */

    @PostMapping("/saveStudentIntroduce")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse saveStudentIntroduce(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String introduce = dataRequest.getString("introduce");
        Optional<Student> sOp = studentRepository.findById(studentId);
        if (!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s = sOp.get();
        Person p = s.getPerson();
        p.setIntroduce(introduce);
        personRepository.save(p);
        return CommonMethod.getReturnMessageOK();
    }


    public String importFeeData(Integer studentId,InputStream in){
        try {
        Student student = studentRepository.findById(studentId).get();
        XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        Row row;
        Cell cell;
        int i;
        i = 1;
        String day, money;
        Optional<Fee> fOp;
        Double dMoney;
        Fee f;
        rowIterator.next();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            cell = row.getCell(0);
            if (cell == null)
                break;
            day = cell.getStringCellValue();  //获取一行消费记录 日期 金额
            cell = row.getCell(1);
            money = cell.getStringCellValue();
            fOp = feeRepository.findByStudentStudentIdAndDay(studentId, day);  //查询是否存在记录
            if (!fOp.isPresent()) {
                f = new Fee();
                f.setDay(day);
                f.setStudent(student);  //不存在 添加
            } else {
                f = fOp.get();  //存在 更新
            }
            if (money != null && money.length() > 0)
                dMoney = Double.parseDouble(money);
            else
                dMoney = 0d;
            f.setMoney(dMoney);
            feeRepository.save(f);
        }
        workbook.close();  //关闭Excl输入流
            return null;
    } catch (Exception e) {
        e.printStackTrace();
        return "上传错误！";
    }

}
    /**
     * importFeeData 前端上传消费流水Excl表数据服务
     *
     * @param barr         文件二进制数据
     * @param uploader     上传者
     * @param studentIdStr student 主键
     * @param fileName     前端上传的文件名
     * @return
     */
    @PostMapping(path = "/importFeeData")
    public DataResponse importFeeData(@RequestBody byte[] barr,
                                      @RequestParam(name = "uploader") String uploader,
                                      @RequestParam(name = "studentId") String studentIdStr,
                                      @RequestParam(name = "fileName") String fileName) {
        Integer studentId =  Integer.parseInt(studentIdStr);
        String msg = importFeeData(studentId,new ByteArrayInputStream(barr));
        if(msg == null)
            return CommonMethod.getReturnMessageOK();
        else
            return CommonMethod.getReturnMessageError(msg);
    }

    /**
     * getStudentListExcl 前端下载导出学生基本信息Excl表数据
     *
     * @param dataRequest
     * @return
     */
    @PostMapping("/getStudentListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getStudentListExcl(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List list = getStudentMapList(numName);
        Integer widths[] = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String titles[] = {"序号", "学号", "姓名", "学院", "专业", "班级", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址"};
        String outPutSheetName = "student.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for (j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
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
        if (list != null && list.size() > 0) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = (Map) list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "major"));
                cell[5].setCellValue(CommonMethod.getString(m, "className"));
                cell[6].setCellValue(CommonMethod.getString(m, "card"));
                cell[7].setCellValue(CommonMethod.getString(m, "genderName"));
                cell[8].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[9].setCellValue(CommonMethod.getString(m, "email"));
                cell[10].setCellValue(CommonMethod.getString(m, "phone"));
                cell[11].setCellValue(CommonMethod.getString(m, "address"));
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

    /**
     * getStudentIntroducePdf 生成获取个人简历的PDF数据流服务
     *
     * @param dataRequest studentId 学生主键
     * @return 返回PDF文件二进制数据
     */
    @PostMapping("/getStudentIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getStudentIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Student s = studentRepository.getById(studentId);  //查询获得Student对象
        Map info = studentService.getMapFromStudent(s); //获得学生信息
        String content = (String) info.get("introduce");  // 个人简历的HTML字符串
        content = CommonMethod.addHeadInfo(content, "<style> html { font-family: \"SourceHanSansSC\", \"Open Sans\";}  </style> <meta charset='UTF-8' />  <title>Insert title here</title>");  // 插入由HTML转换PDF需要的头信息
        System.out.println(content);
        content = CommonMethod.removeErrorString(content, "&nbsp;", "style=\"font-family: &quot;&quot;;\""); //删除无法转化不合法的HTML标签
        content = CommonMethod.replaceNameValue(content, info); //将HTML中标记串${name}等替换成学生实际的信息
        return baseService.getPdfDataFromHtml(content); //生成学生简历PDF二进制数据
    }

    @PostMapping("/getStudentPageData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getStudentPageData(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List dataList = new ArrayList();
        Page<Student> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = studentRepository.findStudentPageByNumName(numName, pageable);
        Map m;
        Student s;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List list = page.getContent();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    s = (Student) list.get(i);
                    m = studentService.getMapFromStudent(s);
                    dataList.add(m);
                }
            }
        }
        HashMap data = new HashMap();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }


    public byte[] getStudentIntroduceItextPdfData(Integer studentId) {
        byte data[] = null;
        try {
            Map<String, Object> map = new HashMap<>();
            //设置纸张规格为A4纸
            Rectangle rect = new Rectangle(PageSize.A4);
            //创建文档实例
            Document doc = new Document(rect);
            //添加中文字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            //设置字体样式
            Font textFont = new Font(bfChinese, 11, Font.NORMAL); //正常
            //Font redTextFont = new Font(bfChinese,11,Font.NORMAL,Color.RED); //正常,红色
            Font boldFont = new Font(bfChinese, 11, Font.BOLD); //加粗
            //Font redBoldFont = new Font(bfChinese,11,Font.BOLD,Color.RED); //加粗,红色
            Font firsetTitleFont = new Font(bfChinese, 22, Font.BOLD); //一级标题
            Font secondTitleFont = new Font(bfChinese, 15, Font.BOLD, CMYKColor.BLUE); //二级标题
            Font underlineFont = new Font(bfChinese, 11, Font.UNDERLINE); //下划线斜体
            //设置字体
            com.itextpdf.text.Font FontChinese24 = new com.itextpdf.text.Font(bfChinese, 24, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese16 = new com.itextpdf.text.Font(bfChinese, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese12 = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font FontChinese11Bold = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font FontChinese11 = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.ITALIC);
            com.itextpdf.text.Font FontChinese11Normal = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //设置要导出的pdf的标题
            String title = "霸道流氓气质";
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.newPage();
            //新建段落
            //使用二级标题 颜色为蓝色
            Paragraph p1 = new Paragraph("二级标题", secondTitleFont);
            //设置行高
            p1.setLeading(0);
            //设置标题居中
            p1.setAlignment(Element.ALIGN_CENTER);
            //将段落添加到文档上
            doc.add(p1);
            //设置一个空的段落，行高为18  什么内容都不显示
            Paragraph blankRow1 = new Paragraph(18f, " ", FontChinese11);
            doc.add(blankRow1);
            //新建表格 列数为2
            PdfPTable table1 = new PdfPTable(2);
            //给表格设置宽度
            int width1[] = {80, 60};
            table1.setWidths(width1);
            //新建单元格
            String name = "霸道";
            String gender = "男";
            //给单元格赋值 每个单元格为一个段落，每个段落的字体为加粗
            PdfPCell cell11 = new PdfPCell(new Paragraph("姓名：  " + name, boldFont));
            PdfPCell cell12 = new PdfPCell(new Paragraph("性别：  " + gender, boldFont));
            //设置单元格边框为0
            cell11.setBorder(0);
            cell12.setBorder(0);
            table1.addCell(cell11);
            table1.addCell(cell12);
            doc.add(table1);
            PdfPTable table3 = new PdfPTable(2);
            table3.setWidths(width1);
            PdfPCell cell15 = new PdfPCell(new Paragraph("博客主页： https://me.csdn.net/BADAO_LIUMANG_QIZHI  ", boldFont));
            PdfPCell cell16 = new PdfPCell(new Paragraph("当前时间：  " + DateTimeTool.parseDateTime(new Date(), "yyy_MM_dd"), boldFont));
            cell15.setBorder(0);
            cell16.setBorder(0);
            table3.addCell(cell15);
            table3.addCell(cell16);
            doc.add(table3);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/getStudentIntroduceItextPdf")
    public ResponseEntity<StreamingResponseBody> getStudentIntroduceItextPdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        return CommonMethod.getByteDataResponseBodyPdf(getStudentIntroduceItextPdfData(studentId));
    }

    public void exportPdfServlet(Long orderId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //设置响应格式等
        response.setContentType("application/pdf");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        Map<String, Object> map = new HashMap<>();
        //设置纸张规格为A4纸
        Rectangle rect = new Rectangle(PageSize.A4);
        //创建文档实例
        Document doc = new Document(rect);
        //添加中文字体
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        //设置字体样式
        Font textFont = new Font(bfChinese, 11, Font.NORMAL); //正常
        //Font redTextFont = new Font(bfChinese,11,Font.NORMAL,Color.RED); //正常,红色
        Font boldFont = new Font(bfChinese, 11, Font.BOLD); //加粗
        //Font redBoldFont = new Font(bfChinese,11,Font.BOLD,Color.RED); //加粗,红色
        Font firsetTitleFont = new Font(bfChinese, 22, Font.BOLD); //一级标题
        Font secondTitleFont = new Font(bfChinese, 15, Font.BOLD, CMYKColor.BLUE); //二级标题
        Font underlineFont = new Font(bfChinese, 11, Font.UNDERLINE); //下划线斜体
        //设置字体
        com.itextpdf.text.Font FontChinese24 = new com.itextpdf.text.Font(bfChinese, 24, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese16 = new com.itextpdf.text.Font(bfChinese, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese12 = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Font FontChinese11Bold = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese11 = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.ITALIC);
        com.itextpdf.text.Font FontChinese11Normal = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);

        //设置要导出的pdf的标题
        String title = "霸道流氓气质";
        response.setHeader("Content-disposition", "attachment; filename=".concat(String.valueOf(URLEncoder.encode(title + ".pdf", "UTF-8"))));
        OutputStream out = response.getOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();
        doc.newPage();
        //新建段落
        //使用二级标题 颜色为蓝色
        Paragraph p1 = new Paragraph("二级标题", secondTitleFont);
        //设置行高
        p1.setLeading(0);
        //设置标题居中
        p1.setAlignment(Element.ALIGN_CENTER);
        //将段落添加到文档上
        doc.add(p1);
        //设置一个空的段落，行高为18  什么内容都不显示
        Paragraph blankRow1 = new Paragraph(18f, " ", FontChinese11);
        doc.add(blankRow1);
        //新建表格 列数为2
        PdfPTable table1 = new PdfPTable(2);
        //给表格设置宽度
        int width1[] = {80, 60};
        table1.setWidths(width1);
        //新建单元格
        String name = "霸道";
        String gender = "男";
        //给单元格赋值 每个单元格为一个段落，每个段落的字体为加粗
        PdfPCell cell11 = new PdfPCell(new Paragraph("姓名：  " + name, boldFont));
        PdfPCell cell12 = new PdfPCell(new Paragraph("性别：  " + gender, boldFont));
        //设置单元格边框为0
        cell11.setBorder(0);
        cell12.setBorder(0);
        table1.addCell(cell11);
        table1.addCell(cell12);
        doc.add(table1);
        PdfPTable table3 = new PdfPTable(2);
        table3.setWidths(width1);
        PdfPCell cell15 = new PdfPCell(new Paragraph("博客主页： https://me.csdn.net/BADAO_LIUMANG_QIZHI  ", boldFont));
        PdfPCell cell16 = new PdfPCell(new Paragraph("当前时间：  " + DateTimeTool.parseDateTime(new Date(), "yyy_MM_dd"), boldFont));
        cell15.setBorder(0);
        cell16.setBorder(0);
        table3.addCell(cell15);
        table3.addCell(cell16);
        doc.add(table3);
        doc.close();
    }

    /*
        FamilyMember
     */
    @PostMapping("/getFamilyMemberList")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse getFamilyMemberList(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        List<FamilyMember> fList = familyMemberRepository.findByStudentStudentId(studentId);
        List dataList = new ArrayList();
        Map m;
        if (fList != null) {
            for (FamilyMember f : fList) {
                m = new HashMap();
                m.put("memberId", f.getMemberId());
                m.put("studentId", f.getStudent().getStudentId());
                m.put("relation", f.getRelation());
                m.put("name", f.getName());
                m.put("gender", f.getGender());
                m.put("age", f.getAge());
                m.put("unit", f.getUnit());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/familyMemberSave")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberSave(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer memberId = CommonMethod.getInteger(form,"memberId");
        Optional<FamilyMember> op;
        FamilyMember f = null;
        if(memberId != null) {
            op = familyMemberRepository.findById(memberId);
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f== null) {
            f = new FamilyMember();
            f.setStudent(studentRepository.findById(studentId).get());
        }
        f.setRelation(CommonMethod.getString(form,"relation"));
        f.setName(CommonMethod.getString(form,"name"));
        f.setGender(CommonMethod.getString(form,"gender"));
        f.setAge(CommonMethod.getInteger(form,"age"));
        f.setUnit(CommonMethod.getString(form,"unit"));
        familyMemberRepository.save(f);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/familyMemberDelete")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Optional<FamilyMember> op;
        op = familyMemberRepository.findById(memberId);
        if(op.isPresent()) {
            familyMemberRepository.delete(op.get());
        }
        return CommonMethod.getReturnMessageOK();
    }


    @PostMapping("/importFeeDataWeb")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse importFeeDataWeb(@RequestParam Map request, @RequestParam("file") MultipartFile file) {
        Integer studentId = CommonMethod.getInteger(request, "studentId");
        try {
            String msg= importFeeData(studentId,file.getInputStream());
            if(msg == null)
                return CommonMethod.getReturnMessageOK();
            else
                return CommonMethod.getReturnMessageError(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageError("上传错误！");
    }

}

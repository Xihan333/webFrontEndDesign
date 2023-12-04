package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class TeachController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Value("${attach.folder}")
    private String attachFolder;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private AwardRepository awardRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private SocialPracticeRepository socialPracticeRepository;
    @Autowired
    private UserTagRepository userTagRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Student s;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("age",s.getAge());
            m.put("dept",s.getDept());
            m.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("EMail", s.getEMail());
            m.put("telephone", s.getTelephone());
            m.put("selfAssessment", s.getSelfAssessment());
            m.put("personalSignature", s.getPersonalSignature());
            m.put("major", s.getMajor());
            String image0  = getPersonImageString(attachFolder,s.getStudentNum(), "0");
            m.put("image0", image0);
            dataList.add(m);
        }
        return dataList;
    }

    //student页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentInit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public DataResponse studentInit(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        if (username == null) username = "";
        List dataList = getStudentMapList(username);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //student页面点击查询按钮请求StudentIntroduce
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentQuery")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') ")
    public DataResponse studentQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/studentDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Student s= null;
        Optional<Student> op;
        if(id != null) {
            op= studentRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            //删除相关数据
            List<Activity> activity = activityRepository.findActivityListByStudentId(id);
            for (int i = 0; i < activity.size(); i++) {
                activityRepository.delete(activity.get(i));
            }

            List<Award> award = awardRepository.findAwardListByStudentId(id);
            for (int i = 0; i < award.size(); i++) {
                awardRepository.delete(award.get(i));
            }

            List<Blog> blog = blogRepository.findBlogListByStudentId(id);
            for (int i = 0; i < blog.size(); i++) {
                blogRepository.delete(blog.get(i));
            }

            List<Evaluation> evaluation1 = evaluationRepository.findByStudentId(id);
            for (int i = 0; i < evaluation1.size(); i++) {
                evaluationRepository.delete(evaluation1.get(i));
            }
            List<Evaluation> evaluation2 = evaluationRepository.findEvaluationListByEvaluatorId(id);
            for (int i = 0; i < evaluation2.size(); i++) {
                evaluationRepository.delete(evaluation2.get(i));
            }

            List<Score> score = scoreRepository.findByStudentId(id);
            for (int i = 0; i < score.size(); i++) {
                scoreRepository.delete(score.get(i));
            }

            List<SocialPractice> socialPractice = socialPracticeRepository.findSocialPracticeListByStudentId(id);
            for (int i = 0; i < socialPractice.size(); i++) {
                socialPracticeRepository.delete(socialPractice.get(i));
            }

            List<UserTag> userTag1 = userTagRepository.findUserTagListByStudentId(id);
            for (int i = 0; i < userTag1.size(); i++) {
                userTagRepository.delete(userTag1.get(i));
            }

            List<UserTag> userTag2 = userTagRepository.findUserTagListByTaggerId(id);
            for (int i = 0; i < userTag2.size(); i++) {
                userTagRepository.delete(userTag2.get(i));
            }
            List<User> user = userRepository.findUserListByStudentId(id);
            for (int i = 0; i < user.size(); i++) {
                userRepository.delete(user.get(i));
            }
            studentRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/studentEditInit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse studentEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Student s= null;
        Optional<Student> op;
        if(id != null) {
            op= studentRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        List sexList = new ArrayList();
        Map m;
        m = new HashMap();
        m.put("label","男");
        m.put("value","1");
        sexList.add(m);
        m = new HashMap();
        m.put("label","女");
        m.put("value","2");
        sexList.add(m);
        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("studentNum",s.getStudentNum());
            form.put("studentName",s.getStudentName());
            form.put("sex",s.getSex());  //这里不需要转换
            form.put("dept",s.getDept());
            form.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd")); //这里需要转换为字符串
            form.put("EMail", s.getEMail());
            form.put("telephone", s.getTelephone());
            form.put("selfAssessment", s.getSelfAssessment());
            form.put("personalSignature", s.getPersonalSignature());
            form.put("major", s.getMajor());
            image0  = getPersonImageString(attachFolder,s.getStudentNum(), "0");
        }
        form.put("sexList",sexList);
        Map data = new HashMap();
        data.put("form",form);
        data.put("sexList",sexList);
        data.put("image0",image0);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
//  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewStudentId(){
        Integer
        id = studentRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/studentEditSubmit")
    @PreAuthorize(" hasRole('USER') or hasRole('ADMIN')")
    public DataResponse studentEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String studentNum = CommonMethod.getString(form,"studentNum");  //Map 获取属性的值
        String studentName = CommonMethod.getString(form,"studentName");
        String sex = CommonMethod.getString(form,"sex");
        String dept = CommonMethod.getString(form, "dept");
        Date birthday = CommonMethod.getDate(form,"birthday");
        String EMail = CommonMethod.getString(form, "EMail");
        String telephone = CommonMethod.getString(form, "telephone");
        String major = CommonMethod.getString(form, "major");
        String selfAssessment = CommonMethod.getString(form, "selfAssessment");
        String personalSignature = CommonMethod.getString(form, "personalSignature");
        if (Objects.equals(studentName, "") || Objects.equals(studentNum, "")) {
            return CommonMethod.getReturnMessageError("姓名或者学号不能为空");
        }
        if (id == null && studentRepository.findByStudentNum(studentNum).isPresent()) {
            return CommonMethod.getReturnMessageError("学号重复！");
        }
        if (telephone.length() > 11) {
            return CommonMethod.getReturnMessageError("手机号不合法！");
        }
        if (personalSignature.length() > 40)
            return CommonMethod.getReturnMessageError("个性签名不能大于40个字");
        Integer age;
        if (birthday != null) {
            Date now = new Date();
            long d1 = now.getTime();
            long d2 = birthday.getTime();
            age = (int)((d1 - d2) / 1000 / 60 / 60 / 24 / 365);
        }else {
            age = null;
        }
        Student s= null;
        Optional<Student> op;
        if(id != null) {
            op= studentRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Student();   //不存在 创建实体对象
            id = getNewStudentId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setStudentNum(studentNum);  //设置属性
        s.setStudentName(studentName);
        s.setSex(sex);
        s.setAge(age);
        s.setDept(dept);
        s.setBirthday(birthday);
        s.setEMail(EMail);
        s.setTelephone(telephone);
        s.setMajor(major);
        s.setPersonalSignature(personalSignature);
        s.setSelfAssessment(selfAssessment);
        studentRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }


    //  学生个人简历页面
    //在系统在主界面内点击个人简历，后台准备个人简历所需要的各类数据组成的段落数据，在前端显示
    @PostMapping("/getStudentIntroduceData")
    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map data = introduceService.getIntroduceDataMap(studentId);
        return CommonMethod.getReturnData(data);  //返回前端个人简历数据
    }

    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.useFastMode();
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf");
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        }
        catch (Exception e) {
            return  ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/getStudentIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getStudentIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map data = introduceService.getIntroduceDataMap(studentId);
        String content= "<!DOCTYPE html>";
        content += "<html>";
        content += "<head>";
        content += "<style>";
        content += "html { font-family: \"SourceHanSansSC\", \"Open Sans\";}";
        content += "</style>";
        content += "<meta charset='UTF-8' />";
        content += "<title>Insert title here</title>";
        content += "</head>";

        String myName = (String) data.get("myName");
        String overview = (String) data.get("overview");
        List<Map> attachList = (List) data.get("attachList");
        content += "<body>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        "+myName+" </tr>";
        content += "   </thead>";
        content += "   </table>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        "+overview+" </tr>";
        content += "   </thead>";
        content += "   </table>";

        content += "<table style='width: 100%;border-collapse: collapse;border: 1px solid black;'>";
        content +=   " <tbody>";

        for(int i = 0; i <attachList.size(); i++ ){
            content += "     <tr style='text-align: center;border: 1px solid black;font-size: 14px;'>";
            content += "      "+attachList.get(i).get("title")+" ";
            content += "     </tr>";
            content += "     <tr style='text-align: center;border: 1px solid black; font-size: 14px;'>";
            content += "            "+attachList.get(i).get("content")+" ";
            content += "     </tr>";
        }
        content +=   " </tbody>";
        content += "   </table>";

        content += "</body>";
        content += "</html>";
        return getPdfDataFromHtml(content);
    }


    @PostMapping("/uploadPersonImage")
    @PreAuthorize("hasRole('USER2') or hasRole('USER') or hasRole('ADMIN')")
    public DataResponse uploadPersonImage(@RequestParam Map pars, @RequestParam("file") MultipartFile file) {
        String studentNum = CommonMethod.getString(pars,"studentNum");
        String no = CommonMethod.getString(pars,"no");
        String oFileName = file.getOriginalFilename();
        oFileName = oFileName.toUpperCase();
        try{
            InputStream in = file.getInputStream();
            int size = (int)file.getSize();
            byte [] data = new byte[size];
            in.read(data);
            in.close();
            String fileName =attachFolder + "images/" + studentNum + "-" + no + ".JPG";
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(data);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageOK();
    }
    public String getPersonImageString(String attachFolder, String studentNum, String no) {
        String fileName =attachFolder + "images/" + studentNum + "-" + no + ".JPG";
        File file = new File(fileName);
        if (!file.exists())
            return "";
        try {
            FileInputStream in = new FileInputStream(file);
            int size = (int) file.length();
            byte data[] = new byte[size];
            in.read(data);
            in.close();
            String imgStr = "data:image/png;base64,";
            String s = new String(Base64.getEncoder().encode(data));
            imgStr = imgStr + s;
            return imgStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/getPersonImage")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getPersonImage(@Valid @RequestBody DataRequest dataRequest) {
        String  studentNum= dataRequest.getString("studentNum");
        String no = dataRequest.getString("no");
        String str = getPersonImageString(attachFolder,studentNum, no);
        return CommonMethod.getReturnData(str);
    }


    @PostMapping("/studentIntroduceInit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse studentIntroduceInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Student s= null;
        Optional<Student> op;
        if(id != null) {
            op= studentRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        //这里获取List
        List socialPracticeList = getSocialPracticeMapList(id);
        List awardList = getAwardMapList(id);
        List activityList = getActivityMapList(id);
        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("studentName",s.getStudentName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                form.put("sex","男");
            }else {
                form.put("sex","女");
            }
            form.put("age",s.getAge());
            form.put("telephone", s.getTelephone());
            form.put("dept",s.getDept());
            form.put("major", s.getMajor());
            form.put("EMail", s.getEMail());
            form.put("gpa", String.format("%.2f", getGpa(id)));
            form.put("curriculum", getCurriculum(id));
            form.put("selfAssessment", s.getSelfAssessment());
            form.put("personalSignature", s.getPersonalSignature());
            image0  = getPersonImageString(attachFolder,s.getStudentNum(), "0");
        }
        Map data = new HashMap();
        data.put("form",form);
        data.put("image0",image0);
        data.put("socialPracticeList", socialPracticeList);
        data.put("awardList", awardList);
        data.put("activityList", activityList);

        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    private Double getGpa(Integer id){
        List<Score> scores = scoreRepository.findByStudentId(id);
        if (scores.isEmpty())
            return 0.0;
        String str = "";
        Double sumCredit = 0.00001;
        double sum = 0.0;
        for (int i = 0; i < scores.size(); i++) {
            sum += scores.get(i).getCourse().getCredit() * scores.get(i).getGradePoint();
            sumCredit += scores.get(i).getCourse().getCredit();
        }
        if (sumCredit == 0) {
            return 0.0;
        }
        double v = sum / sumCredit;
        return v;
    }
    private String getCurriculum(Integer id) {
        List<Score> scores = scoreRepository.findByStudentId(id);
        String str = "";
        for (int i = 0; i < scores.size(); i++) {
            if (i == 0)
                str += scores.get(i).getCourse().getCourseName();
            else
                str = str + ", " + scores.get(i).getCourse().getCourseName();
        }
        return str;
    }
    private List getSocialPracticeMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<SocialPractice> sList = socialPracticeRepository.findSocialPracticeListByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        SocialPractice s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("socialPracticeName",s.getSocialPracticeName());
            m.put("content", s.getContent());
            m.put("socialPracticeTime", DateTimeTool.parseDateTime(s.getSocialPracticeTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("outcome", s.getOutcome());
            m.put("address", s.getAddress());
            dataList.add(m);
        }
        return dataList;
    }
    private List getAwardMapList(Integer studentId) {
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
            m.put("info",s.getInfo());
            m.put("awardTime", DateTimeTool.parseDateTime(s.getAwardTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    private List getActivityMapList(Integer studentId) {
        List dataList = new ArrayList();
        List<Activity> sList = activityRepository.findActivityListByStudentId(studentId);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Activity s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("activityName",s.getActivityName());
            m.put("content", s.getContent());
            m.put("activityTime", DateTimeTool.parseDateTime(s.getActivityTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("outcome", s.getOutcome());
            m.put("address", s.getAddress());
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/studentHomePageInit2")
    @PreAuthorize("hasRole('USER')")
    public DataResponse studentHomePageInit2(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        Student s= null;
        Optional<Student> op;
        if(username != null) {
            op= studentRepository.findByStudentNum(username);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        //这里获取List
        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("id", s.getId());
            form.put("studentName",s.getStudentName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                form.put("sex","男");
            }else {
                form.put("sex","女");
            }
            form.put("age",s.getAge());
            form.put("dept",s.getDept());
            form.put("major", s.getMajor());
            form.put("studentNum", s.getStudentNum());
            form.put("telephone", s.getTelephone());
            form.put("EMail", s.getEMail());
            form.put("gpa", String.format("%.2f", getGpa(s.getId())));
            form.put("aInfo1", getActivityInformation1(s.getId()));
            form.put("aInfo2", getActivityInformation2(s.getId()));
            form.put("tagInfo", getTagInformation(s.getId()));
            form.put("personalSignature", s.getPersonalSignature());
            image0  = getPersonImageString(attachFolder,s.getStudentNum(), "0");
        }
        Map data = new HashMap();
        data.put("form",form);
        data.put("image0",image0);

        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
    private String getActivityInformation1(Integer id) {
        String str = "";
        List<Activity> activities = activityRepository.findActivityListByStudentId(id);
        List<Award> awards = awardRepository.findAwardListByStudentId(id);
        List<SocialPractice> socialPractices = socialPracticeRepository.findSocialPracticeListByStudentId(id);
        if (activities.size() >= 3) {
            str += "活动达人 ";
        }
        if (awards.size() >= 3){
            str += "奖励哥 ";
        }
        if (socialPractices.size() >= 3) {
            str += "社交牛人 ";
        }
        double gpa = getGpa(id);
        if (gpa >= 4.0) {
            str += "学霸 ";
        }else if (gpa >= 3.0) {
            str += "优秀大学生 ";
        }else if (gpa >= 1.0) {
            str += "普通大学生 ";
        }
        if (activities.size() + awards.size() + socialPractices.size()  < 2) {
            str += "阿宅";
        }
        if (activities.size() + awards.size() + socialPractices.size()  < 2 && gpa >= 3.5) {
            str += "技术宅";
        }
        return str;
    }
    private String getActivityInformation2(Integer id) {
        String str = "";
        List<Activity> activities = activityRepository.findActivityListByStudentId(id);
        List<Award> awards = awardRepository.findAwardListByStudentId(id);
        List<SocialPractice> socialPractices = socialPracticeRepository.findSocialPracticeListByStudentId(id);
        str = "社会实践*" +socialPractices.size()+ "，校园活动*" +activities.size()+ "，荣誉奖励*" + awards.size();
        return str;
    }
    private String getTagInformation(Integer id) {
        String str = "";
        List<Tag> allTag = tagRepository.findAll();
        int[] count = new int[allTag.size()];
        for (int i = 0; i < allTag.size(); i++) {
            count[i] = 0;
        }
        List<UserTag> userTagList = userTagRepository.findUserTagListByStudentId(id);
        for (int i = 0;  i < userTagList.size(); i++) {
            for (int j = 0; j < allTag.size(); j++){
                if (Objects.equals(userTagList.get(i).getTag().getTag(), allTag.get(j).getTag())) {
                    count[j]++;
                    break;
                }
            }
        }
        for (int i = 0; i < allTag.size(); i++) {
            if (count[i] != 0) {
                str += allTag.get(i).getTag() + "*" + count[i] + " ";
            }
        }
        return str;
    }
    @PostMapping("/studentHomePageInit1")
    @PreAuthorize("hasRole('USER')")
    public DataResponse studentHomePageInit1(@Valid @RequestBody DataRequest dataRequest) {
        String username = dataRequest.getString("username");
        List dataList = getClassScheduleCardMapList(username);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    public List getClassScheduleCardMapList(String studentNum) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findScoreListByStudentNum(studentNum);//数据库查询操作
        Map m;
        if(sList == null || sList.size() == 0){
            for(int i = 0; i < 5;i++) {
                m = new HashMap();
                m.put("count", "第" + (i + 1) + "节");
                m.put("monday", "");
                m.put("tuesday", "");
                m.put("wednesday", "");
                m.put("thursday", "");
                m.put("friday", "");
                m.put("saturday", "");
                m.put("sunday", "");
                dataList.add(m);
            }
            return dataList;
        }
        Score s;
        Course c;
        int count = 0;
        s = sList.get(count);
        for(int i = 0; i < 5;i++) {
            m = new HashMap();
            m.put("count", "第" + (i + 1) + "节");
            if (scoreRepository.findBySessions(studentNum, 1, i+1).isPresent()) {
                m.put("monday",scoreRepository.findBySessions(studentNum, 1, i+1).get().getCourse().getCourseName());
            }else {
                m.put("monday", "");
            }
            if (scoreRepository.findBySessions(studentNum, 2, i+1).isPresent()) {
                m.put("tuesday",scoreRepository.findBySessions(studentNum, 2, i+1).get().getCourse().getCourseName());
            }else {
                m.put("tuesday", "");
            }
            if (scoreRepository.findBySessions(studentNum, 3, i+1).isPresent()) {
                m.put("wednesday",scoreRepository.findBySessions(studentNum, 3, i+1).get().getCourse().getCourseName());
            }else {
                m.put("wednesday", "");
            }
            if (scoreRepository.findBySessions(studentNum, 4, i+1).isPresent()) {
                m.put("thursday",scoreRepository.findBySessions(studentNum, 4, i+1).get().getCourse().getCourseName());
            }else {
                m.put("thursday", "");
            }
            if (scoreRepository.findBySessions(studentNum, 5, i+1).isPresent()) {
                m.put("friday",scoreRepository.findBySessions(studentNum, 5, i+1).get().getCourse().getCourseName());
            }else {
                m.put("friday", "");
            }
            if (scoreRepository.findBySessions(studentNum, 6, i+1).isPresent()) {
                m.put("saturday",scoreRepository.findBySessions(studentNum, 6, i+1).get().getCourse().getCourseName());
            }else {
                m.put("saturday", "");
            }
            if (scoreRepository.findBySessions(studentNum, 7, i+1).isPresent()) {
                m.put("sunday",scoreRepository.findBySessions(studentNum, 7, i+1).get().getCourse().getCourseName());
            }else {
                m.put("sunday", "");
            }
            dataList.add(m);
        }
        return dataList;
    }
}

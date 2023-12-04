package org.fatmansoft.teach.service.system;

import org.apache.catalina.connector.Response;
import org.fatmansoft.teach.cache.IGlobalCache;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.system.EUserType;
import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.system.UserType;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.JwtResponse;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.security.jwt.JwtUtils;
import org.fatmansoft.teach.security.services.UserDetailsImpl;
import org.fatmansoft.teach.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTypeRepository userTypeRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    IGlobalCache iGlobalCache;

    private static final Logger logger = LoggerFactory
            .getLogger(AuthService.class);
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String fromEmail;


    public DataResponse sendEmail(DataRequest dataRequest) {
        String email = dataRequest.getString("email");
        String subject = dataRequest.getString("subject");
        logger.info("HTML email sending start");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 生成邮件验证码
        String mailVerificationCode = EmailUtil.generateRandomString(6);
        try {
            // Set multipart mime message true
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                    true);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);

            String html = "<html>"
                    + "<head>"
                    + "    <style>"
                    + "        body {"
                    + "            font-family: Arial, sans-serif;"
                    + "            background-color: #f5f5f5;"
                    + "            padding: 20px;"
                    + "        }"
                    + "        h1 {"
                    + "            color: #333;"
                    + "            text-align: center;"
                    + "        }"
                    + "        .container {"
                    + "            text-align:center;"
                    + "            width: 90%;"
                    + "            height: 50%"
                    + "            margin: 0 auto;"
                    + "            background-color: #fff;"
                    + "            padding: 20px;"
                    + "            border-radius: 5px;"
                    + "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);"
                    + "        }"
                    + "        .code.b {"
                    + "            color: darkseagreen;"
                    + "            font-size: 300%;"
                    + "            border-radius: 3px;"
                    + "        }"
                    + "    </style>"
                    + "</head>"
                    + "<body>"
                    + "    <div class='container'>"
                    + "        <h1>欢迎来到学生管理系统</h1>"
                    + "        <p class='code'>验证码<b>" + mailVerificationCode + "</b> </p>"
                    + "        <p>您的操作需要您提供接收到的验证码，验证码在5分钟内有效。</p>"
                    + "        <p>如果您没有进行过获取验证码的操作，请忽略这封邮件。</p>"
                    + "        <p>祝您生活愉快！</p>"
                    + "    </div>"
                    + "</body>"
                    + "</html>";

            mimeMessageHelper.setText(html, true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.error("Exeception=>sendHTMLEmail ", e);
        }
        // 存入Redis
        String redisIKey = "MailVerificationCode-" + email;
        try {
            iGlobalCache.set(redisIKey, mailVerificationCode, Const.MAIL_VERIFICATION_CODE_EXPIRE_TIME);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return CommonMethod.getReturnMessageError("Redis存储异常");
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse login(DataRequest dataRequest){
        String username = dataRequest.getString("username");
        String password = dataRequest.getString("password");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Optional<User> op= userRepository.findByUserName(username);
        if(op.isPresent()) {
            User user = op.get();
            user.setLastLoginTime(DateTimeTool.parseDateTime(new Date()));
            Integer count = user.getLoginCount();
            if(count == null){
                count = 1;
            }else{
                count += 1;
            }
            user.setLoginCount(count);
            userRepository.save(user);
        }
        return CommonMethod.getReturnData(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles.get(0)));
    }

    public DataResponse register(DataRequest dataRequest){
        String username = dataRequest.getString("username");
        String password = dataRequest.getString("password");
        String name = dataRequest.getString("name");
        String email = dataRequest.getString("email");
        String role = dataRequest.getString("role");
        String mailVerificationCode = dataRequest.getString("mailVerificationCode");
        UserType ut = null;
        Optional<User> uOp = userRepository.findByUserName(username);
        if(uOp.isPresent()) {
            return CommonMethod.getReturnMessageError("用户已经存在，不能注册！");
        }
        if (!email.contains("@")) return CommonMethod.getReturnMessageError("邮箱格式错误！");
        // 校验邮件验证码
        String redisIKey = "MailVerificationCode-" + email;
        String trueCode = (String) iGlobalCache.get(redisIKey);
        if (trueCode == null || !trueCode.equals(mailVerificationCode)) {
            return CommonMethod.getReturnMessageError("验证码有误");
        }
        Person p = new Person();
        p.setNum(username);
        p.setName(name);
        p.setEmail(email);
        if("ADMIN".equals(role)) {
            p.setType("0");
            ut = userTypeRepository.findByName(EUserType.ROLE_ADMIN);
        }else if("STUDENT".equals(role)) {
            p.setType("1");
            ut = userTypeRepository.findByName(EUserType.ROLE_STUDENT);
        }else if("TEACHER".equals(role)) {
            p.setType("2");
            ut = userTypeRepository.findByName(EUserType.ROLE_TEACHER);
        }
        personRepository.saveAndFlush(p);
        User u = new User();
        u.setPerson(p);
        u.setUserType(ut);
        u.setUserName(username);
        u.setPassword(encoder.encode(password));
        u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
        u.setCreatorId(p.getPersonId());
        u.setLoginCount(0);
        userRepository.saveAndFlush(u);
        if("STUDENT".equals(role)) {
            Student s = new Student();   // 创建实体对象
            s.setPerson(p);
            studentRepository.saveAndFlush(s);  //插入新的Student记录
        }else if("TEACHER".equals(role)) {
            Teacher t = new Teacher();   // 创建实体对象
            t.setPerson(p);
            teacherRepository.saveAndFlush(t);  //插入新的Teacher记录
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse resetPassword(DataRequest dataRequest){
        String username = dataRequest.getString("username");
        String email = dataRequest.getString("email");
        String newPassword = dataRequest.getString("newPassword");
        String mailVerificationCode = dataRequest.getString("mailVerificationCode");
        Optional<User> op = userRepository.findByUserName(username);
        if (!op.isPresent())
            return CommonMethod.getReturnMessageError("账户不存在！");  //通知前端操作正常
        User u = op.get();
        Person p = u.getPerson();
        email = p.getEmail();
        if(!email.equals(p.getEmail())) {
            return CommonMethod.getReturnMessageError("邮箱不匹配不能重置！");
        }
        // 校验邮件验证码
        String redisIKey = "MailVerificationCode-" + email;
        String trueCode = (String) iGlobalCache.get(redisIKey);
        if (trueCode == null || !trueCode.equals(mailVerificationCode)) {
            return CommonMethod.getReturnMessageError("验证码有误");
        }
        u.setPassword(encoder.encode(newPassword));
        userRepository.save(u);
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
}

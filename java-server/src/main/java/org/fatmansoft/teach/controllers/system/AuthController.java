package org.fatmansoft.teach.controllers.system;

import java.util.Optional;

import javax.validation.Valid;

import org.fatmansoft.teach.models.system.Person;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserTypeRepository;
import org.fatmansoft.teach.service.system.AuthService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.LoginControlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.security.jwt.JwtUtils;

/**
 *  AuthController 实现 登录和注册Web服务
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
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
    private AuthService authService;


    /**
     *  用户登录
     * @param dataRequest   username 登录名 2022030003  admin password 密码 123456 admin
     * @return   JwtResponse 用户信息， 该信息再后续的web请求时作为请求头的一部分，用于框架的请求服务权限验证
     */
    @PostMapping("/login")
    public DataResponse login(@Valid @RequestBody DataRequest dataRequest) {
        return authService.login(dataRequest);
    }

    /**
     *  注册用户
     * @param dataRequest
     * @return
     */
    @PostMapping("/registerUser")
    public DataResponse registerUser(@Valid @RequestBody DataRequest dataRequest) {
        return authService.register(dataRequest);
    }

    /**
     *  发送邮件验证码
     * @param dataRequest
     * @return
     */
    @PostMapping("/sendEmail")
    public DataResponse sendEmail(@Valid @RequestBody DataRequest dataRequest) {
        return authService.sendEmail(dataRequest);
    }

    @PostMapping("/getValidateCode")
    public DataResponse getValidateCode(@Valid @RequestBody DataRequest dataRequest) {
        return CommonMethod.getReturnData(LoginControlUtil.getInstance().getValidateCodeDataMap());
    }

    @PostMapping("/resetPassWord")
    public DataResponse resetPassWord(@Valid @RequestBody DataRequest dataRequest) {
        return authService.resetPassword(dataRequest);
    }

    @PostMapping("/testValidateInfo")
    public DataResponse testValidateInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer validateCodeId = dataRequest.getInteger("validateCodeId");
        String validateCode = dataRequest.getString("validateCode");
        LoginControlUtil li =  LoginControlUtil.getInstance();
        if(validateCodeId == null || validateCode== null || validateCode.length() == 0) {
            return CommonMethod.getReturnMessageError("验证码为空！");
        }
        String value = li.getValidateCode(validateCodeId);
        System.out.println("value" + value);
        if(!validateCode.equals(value))
            return CommonMethod.getReturnMessageError("验证码错误！");
        return CommonMethod.getReturnMessageOK();
    }


    @GetMapping("/testEmail")
    public DataResponse taetEmail(@Valid @RequestBody DataRequest dataRequest){
        authService.sendEmail(dataRequest);
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

}

package org.fatmansoft.teach.controllers;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.tomcat.util.net.openssl.OpenSSLUtil;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.TeacherRepository;
import org.fatmansoft.teach.repository.UserTypeRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.UimsUtil;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fatmansoft.teach.payload.request.LoginRequest;
import org.fatmansoft.teach.payload.request.SignupRequest;
import org.fatmansoft.teach.payload.response.JwtResponse;
import org.fatmansoft.teach.payload.response.MessageResponse;
import org.fatmansoft.teach.repository.UserRepository;
import org.fatmansoft.teach.security.jwt.JwtUtils;
import org.fatmansoft.teach.security.services.UserDetailsImpl;
import org.yaml.snakeyaml.Yaml;

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
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));


        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles.get(0)));
    }

    public synchronized Integer getNewUserId(){
        Integer id = userRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public synchronized Integer getNewStudentId(){
        Integer
                id = studentRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    public synchronized Integer getNewTeacherId(){
        Integer
                id = teacherRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/signup")//用户注册
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("用户名已被占用!"));
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));
        String role = signUpRequest.getRole();
        Student s = null;
        Teacher t = null;
        if (role == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Role is null!"));
        } else {
            switch (role) {
                case "admin":
                    UserType adminRole = userTypeRepository.findByName(EUserType.ROLE_ADMIN);
                    user.setUserType(adminRole);
                    break;
                case "student":
                    UserType userRole = userTypeRepository.findByName(EUserType.ROLE_USER);
                    user.setUserType(userRole);
                    Optional<Student> os = studentRepository.findByStudentNum(signUpRequest.getUsername());
                    if (os.isPresent()) {
                        user.setStudent(os.get());
                    }else {
                        s = new Student();   //不存在 创建实体对象
                        s.setId(getNewStudentId());  //设置新的id
                        s.setStudentNum(signUpRequest.getUsername());
                        studentRepository.save(s);
                        user.setStudent(s);
                    }
                    break;
                case "teacher":
                    UserType user2Role = userTypeRepository.findByName(EUserType.ROLE_USER2);
                    user.setUserType(user2Role);
                    Optional<Teacher> ot = teacherRepository.findByTeacherNum(signUpRequest.getUsername());
                    if (ot.isPresent()) {
                        user.setTeacher(ot.get());
                    }else {
                        t = new Teacher();   //不存在 创建实体对象
                        t.setId(getNewTeacherId());  //设置新的id
                        t.setTeacherNum(signUpRequest.getUsername());
                        teacherRepository.save(t);
                        user.setTeacher(t);
                    }
                    break;
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("用户类型设置错误"));
                }
            }
        user.setUserId(getNewUserId());
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("用户注册成功!"));
    }
    @PostMapping("/getUimsConfig")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse getUimsConfig(@Valid @RequestBody DataRequest dataRequest) {
        Map data = new HashMap();;
        InputStream in = null;
        try {
            Yaml yaml = new Yaml();
            Resource resource = resourceLoader.getResource("classpath:uims.yml");
            in = resource.getInputStream();
            data =(Map)yaml.load(in);
        }catch(Exception e){

        }
        return CommonMethod.getReturnData(data);
    }

}

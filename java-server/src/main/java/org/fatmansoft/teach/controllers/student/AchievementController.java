package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.models.student.Achievement;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.repository.student.AchievementRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.service.student.AchievementService;
import org.fatmansoft.teach.service.student.StudentService;
import org.fatmansoft.teach.service.teacher.TeacherService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/achievement")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    /**
     *  getAchievementList 学生管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */

    @PostMapping("/getAchievementList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAchievementList(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.getAchievementList(dataRequest);
    }


    /**
     * 获取当前用户的所有成就（学生）
     * @param
     * @return
     */
    @GetMapping("/getStudentAchievement")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse getStudentAchievement() {
        return achievementService.getStudentAchievement();
    }

    /**
     * 删除某一id的成就
     * @param dataRequest   achievementId
     * @return
     */
    @PostMapping("/achievementDelete")
    public DataResponse achievementDelete(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementDelete(dataRequest);
    }

    /**
     * getAchievementInfo 前端点击成就列表时前端获取成就详细信息请求服务
     * @param dataRequest 从前端获取 achievementId 查询成就信息的主键 achievement_id
     * @return  根据achievementId从数据库中查出数据，存在Map对象里，并返回前端
     */

    @PostMapping("/getAchievementInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAchievementInfo(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.getAchievementInfo(dataRequest);
    }

    /**
     * achievementEditSave 前端成就信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * achievementId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改成就的主键 achievement_id 返回前端
     */
    @PostMapping("/achievementEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementEditSave(dataRequest);
    }

    @PostMapping("/achievementStudentEditSave")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse achievementStudentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementStudentEditSave(dataRequest);
    }

    @PostMapping("/show/achievementWaiting")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementWaiting(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = achievementService.getWaitingAchievementMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/achievementPassed")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementPassed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = achievementService.getPassedAchievementMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/show/achievementFailed")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementFailed(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = achievementService.getFailedAchievementMapList();
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/examine/achievementPass")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementPass(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementPass(dataRequest);
    }

    @PostMapping("/examine/achievementFail")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse achievementFail(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.achievementFail(dataRequest);
    }
}

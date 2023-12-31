package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.EvaluationRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.service.student.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping("/getEvaluationList")
    public DataResponse getEvaluationList() {
        return evaluationService.getEvaluationList();
    }

    @PostMapping("/evaluationEditSave")
    public DataResponse EvaluationEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return evaluationService.evaluationEditSave(dataRequest);
    }

    //我是被评估者（获取评论我的评论）
    @GetMapping("/getMyEvaluationList")
    public DataResponse getMyEvaluationList() {
        return evaluationService.getMyEvaluationList();
    }

    //我是评估者（获取我发送的评论）
    @GetMapping("/getMySendEvaluationList")
    public DataResponse getMySendEvaluationList() {
        return evaluationService.getMySendEvaluationList();
    }


    @PostMapping("/evaluationDelete")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DataResponse achievementDelete(@Valid @RequestBody DataRequest dataRequest) {
        return evaluationService.evaluationDelete(dataRequest);
    }

    @PostMapping("/evaluationAdminDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse evaluationAdminDelete(@Valid @RequestBody DataRequest dataRequest) {
        return evaluationService.evaluationAdminDelete(dataRequest);
    }

    /**
     * 获取studentId对应学生收到的所有评论
     * @param dataRequest
     * @return
     */
    @PostMapping("/getOneEvaluationList")
    public DataResponse getOneEvaluationList(@Valid @RequestBody DataRequest dataRequest){
        return evaluationService.getOneEvaluationList(dataRequest);
    }
}

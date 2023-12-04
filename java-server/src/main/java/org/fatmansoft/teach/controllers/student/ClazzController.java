package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Clazz;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.ClazzRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
import org.fatmansoft.teach.service.student.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clazz")
public class ClazzController {

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClazzService clazzService;
    @Autowired
    private StudentRepository studentRepository;

    /**
     * 根据gradeId查找该年级所有班级
     * @param dataRequest
     * @return
     */
    @PostMapping("/getClazzOptionItemListByGradeId")
    public DataResponse getClazzOptionItemListByGradeId(@Valid @RequestBody DataRequest dataRequest) {
        return clazzService.getClazzOptionItemListByGradeId(dataRequest);
    }
}

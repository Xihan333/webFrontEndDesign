package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Clazz;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.ClazzRepository;
import org.fatmansoft.teach.repository.teacher.TeacherRepository;
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
    @PostMapping("/getClazzOptionItemListByGradeId")
    public OptionItemList getClazzOptionItemListByGradeId(@Valid @RequestBody DataRequest dataRequest) {
        Integer gradeId=dataRequest.getInteger("gradeId");
        List<Clazz> cList = clazzRepository.findClazzListByGradeGradeId(gradeId);  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Clazz c : cList) {
            itemList.add(new OptionItem(c.getClazzId(), c.getClazzName(), c.getClazzName()));
        }
        return new OptionItemList(0, itemList);
    }
}

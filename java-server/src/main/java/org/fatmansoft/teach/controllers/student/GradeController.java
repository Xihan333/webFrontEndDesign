package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Grade;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.student.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/grade")
public class GradeController {
    @Autowired
    private GradeRepository gradeRepository;
    @PostMapping("/getGradeOptionItemList")
    public OptionItemList getGradeOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        List<Grade> gList = gradeRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList();
        for (Grade g : gList) {
            itemList.add(new OptionItem(g.getGradeId(), g.getGradeName(), g.getGradeName()));
        }
        return new OptionItemList(0, itemList);
    }

}

package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Info;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.InfoRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class InfoController {
    @Autowired
    private InfoRepository infoRepository;
    @PostMapping("/infoInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse infoInit(@Valid @RequestBody DataRequest dataRequest) {
        String input = dataRequest.getString("input");
        List<Info> list = infoRepository.findByInfo("input");
        List  dataList = new ArrayList();
        Map m;
        Info in;
        if(list != null && list.size() >0) {
            for(int i = 0;i < list.size();i++) {
                m = new HashMap();
                in = list.get(i);
                m.put("id",in.getId());
                m.put("info", in.getInfo());
                m.put("name", in.getName());
            }
        }
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

}

package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.StatisticsDay;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StatisticsDayRepository;
import org.fatmansoft.teach.repository.UserRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatisticsDayRepository statisticsDayRepository;

    @PostMapping("/getMainPageData")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('TEACHER')")
    public DataResponse getMainPageData(@Valid @RequestBody DataRequest dataRequest) {
        Date day = new Date();
        Date monthDay = DateTimeTool.prevMonth(day);
        List nList;
        int i;
        Integer id;
        Object a[];
        Long l;
        String name;
        Long total = userRepository.count();
        Integer monthCount = userRepository.countLastLoginTime(DateTimeTool.parseDateTime(monthDay,"yyyy-MM-dd")+" 00:00:00");
        Integer dayCount = userRepository.countLastLoginTime(DateTimeTool.parseDateTime(day,"yyyy-MM-dd")+" 00:00:00");
        Map data = new HashMap();
        Map m = new HashMap();
        m.put("total",total.intValue());
        m.put("monthCount",monthCount);
        m.put("dayCount",dayCount);
        data.put("onlineUser", m);
        nList = userRepository.getCountList();
        List userTypeList = new ArrayList();
        for(i= 0;i < nList.size();i++) {
            m = new HashMap();
            a = (Object[])nList.get(i);
            id = (Integer)a[0];
            l = (Long)a[1];
            if(id == 1)
                name = "管理员";
            else if(id == 2)
                name = "学生";
            else if(id == 3)
                name = "教师";
            else
                name = "";
            m.put("name", name);
            m.put("value",l.intValue());
            userTypeList.add(m);
        }
        data.put("userTypeList", userTypeList);
        List requestList= new ArrayList();
        List operateList = new ArrayList();
        List<StatisticsDay>sList = statisticsDayRepository.findListByDay(DateTimeTool.parseDateTime(monthDay,"yyyyMMdd"),DateTimeTool.parseDateTime(day,"yyyyMMdd"));
        List<String> dayList = new ArrayList();
        List<String> lList = new ArrayList();
        List<String> rList = new ArrayList();
        List<String> cList = new ArrayList();
        List<String> mList = new ArrayList();
        for(StatisticsDay s:sList) {
            dayList.add(s.getDay());
            lList.add(""+s.getLoginCount());
            rList.add(""+s.getRequestCount());
            cList.add(""+s.getCreateCount());
            mList.add(""+s.getLoginCount());
        }
        m = new HashMap();
        m.put("value",dayList);
        m.put("label1",lList);
        m.put("label2",rList);
        data.put("requestData", m);
        m = new HashMap();
        m.put("value",dayList);
        m.put("label1",cList);
        m.put("label2",mList);
        data.put("operateData", m);



        return CommonMethod.getReturnData(data);
    }
}

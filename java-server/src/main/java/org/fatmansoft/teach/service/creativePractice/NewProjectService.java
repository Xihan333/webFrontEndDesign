package org.fatmansoft.teach.service.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.creativePractice.NewProject;

import org.fatmansoft.teach.repository.creativePratice.NewProjectRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewProjectService {
    @Autowired
    private NewProjectRepository newProjectRepository;

    public Map getMapFromNewProject(NewProject newProject) {
        Map m = new HashMap();
        Student s;
        if (newProject == null)
            return m;
        m.put("newProjectId",newProject.getNewProjectId());
        m.put("groupName",newProject.getGroupName());
        m.put("member",newProject.getMember());
        m.put("don",newProject.getDon());
        m.put("projectName",newProject.getProjectName());
        m.put("isContribute",newProject.getIsContribute());
        m.put("exposition",newProject.getExposition());
        m.put("auditStatus", newProject.getAuditStatus());
        Integer status = newProject.getAuditStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));
        s = newProject.getStudent();
        if (s == null)
            return m;
        m.put("studentId", s.getStudentId());
        m.put("personId", s.getPerson().getPersonId());
        m.put("num", s.getPerson().getNum());
        m.put("name", s.getPerson().getName());
        m.put("dept", s.getPerson().getDept());
        m.put("card", s.getPerson().getCard());
        String gender = s.getPerson().getGender();
        m.put("gender", s.getPerson().getGender());
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", s.getPerson().getBirthday());  //时间格式转换字符串
        m.put("email", s.getPerson().getEmail());
        m.put("phone", s.getPerson().getPhone());
        m.put("address", s.getPerson().getAddress());
        m.put("introduce", s.getPerson().getIntroduce());
        return m;
    }

    /**
     * getNewProjectMapList 根据输入参数查询得到学生创新项目数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getNewProjectMapList(String numName) {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findNewProjectListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        List<NewProject> tList = newProjectRepository.findNewProjectListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromNewProject(tList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询所有创新项目 按编号排序
     */
    public List getAllNewProjectMapList() {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findAllNewProjectList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询审核中状态创新项目 按编号排序
     */
    public List getWaitingNewProjectMapList() {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findWaitingNewProjectList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询已通过状态创新项目 按编号排序
     */
    public List getPassedNewProjectMapList() {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findPassedNewProjectList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询未通过状态创新项目 按编号排序
     */
    public List getFailedNewProjectMapList() {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findFailedNewProjectList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        return dataList;
    }
    public List getNewProjectMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findNewProjectByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        return dataList;
    }
    public List getNewProjectMapListByTeacherId(Integer teacherId) {
        List dataList = new ArrayList();
        List<NewProject> sList = newProjectRepository.findNewProjectByTeacherId(teacherId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromNewProject(sList.get(i)));
        }
        return dataList;
    }
}

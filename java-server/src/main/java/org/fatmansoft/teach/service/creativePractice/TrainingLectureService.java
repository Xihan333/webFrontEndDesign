package org.fatmansoft.teach.service.creativePractice;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.models.creativePractice.TrainingLecture;
import org.fatmansoft.teach.repository.creativePratice.TrainingLectureRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingLectureService {
    @Autowired
    private TrainingLectureRepository trainingLectureRepository;

    public Map getMapFromTrainingLecture(TrainingLecture trainingLecture) {
        Map m = new HashMap();
        Student s;
        Teacher t;
        if (trainingLecture == null)
            return m;
        m.put("trainingLectureId",trainingLecture.getTrainingLectureId());
        m.put("date",trainingLecture.getDate());
        m.put("location",trainingLecture.getLocation());
        m.put("speaker",trainingLecture.getSpeaker());
        m.put("theme",trainingLecture.getTheme());
        m.put("extendFraction",trainingLecture.getExtendFraction());
        m.put("inspiration",trainingLecture.getInspiration());
        m.put("auditStatus", trainingLecture.getAuditStatus());
        Integer status = trainingLecture.getAuditStatus();
        m.put("statusName", ComDataUtil.getInstance().getStatusByValue(status));
        s = trainingLecture.getStudent();
        t = trainingLecture.getTeacher();
        if (s == null && t ==null)
            return m;
        if (s != null) {
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
        }else{
            m.put("personId", t.getPerson().getPersonId());
            m.put("num", t.getPerson().getNum());
            m.put("name", t.getPerson().getName());
            m.put("dept", t.getPerson().getDept());
            m.put("card", t.getPerson().getCard());
            String gender = t.getPerson().getGender();
            m.put("gender", t.getPerson().getGender());
            m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
            m.put("birthday", t.getPerson().getBirthday());  //时间格式转换字符串
            m.put("email", t.getPerson().getEmail());
            m.put("phone", t.getPerson().getPhone());
            m.put("address", t.getPerson().getAddress());
            m.put("introduce", t.getPerson().getIntroduce());
        }
        return m;
    }

    /**
     * getTrainingLectureMapList 根据输入参数查询得到学生科研成果数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     */
    public List getTrainingLectureMapList(String numName) {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findTrainingLectureListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        List<TrainingLecture> tList = trainingLectureRepository.findTrainingLectureListByTeacherNumName(numName);  //数据库查询操作
        if (tList == null || tList.size() == 0)
            return dataList;
        for (int i = 0; i < tList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(tList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询所有科研成果 按编号排序
     */
    public List getAllTrainingLectureMapList() {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findAllTrainingLectureList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询审核中状态科研成果 按编号排序
     */
    public List getWaitingTrainingLectureMapList() {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findWaitingTrainingLectureList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询已通过状态科研成果 按编号排序
     */
    public List getPassedTrainingLectureMapList() {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findPassedTrainingLectureList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        return dataList;
    }

    /**
     * 查询未通过状态科研成果 按编号排序
     */
    public List getFailedTrainingLectureMapList() {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findFailedTrainingLectureList();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        return dataList;
    }
    public List getTrainingLectureMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findTrainingLectureByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        return dataList;
    }
    public List getTrainingLectureMapListByTeacherId(Integer teacherId) {
        List dataList = new ArrayList();
        List<TrainingLecture> sList = trainingLectureRepository.findTrainingLectureByTeacherId(teacherId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromTrainingLecture(sList.get(i)));
        }
        return dataList;
    }
}

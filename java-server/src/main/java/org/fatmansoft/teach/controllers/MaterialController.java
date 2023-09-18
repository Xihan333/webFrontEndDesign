package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.MyTreeNode;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.MaterialRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.TaskStudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/material")
public class MaterialController {
    @Value("${attach.folder}")    //环境配置变量获取
    private String attachFolder;  //服务器端数据存储
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TaskStudentRepository taskStudentRepository;

    @PostMapping("/getMaterialCourseItemOptionList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public OptionItemList getMaterialCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Course> sList = null;
        List<OptionItem> itemList = new ArrayList();
        String role = dataRequest.getString("role");
        if(role.equals("ROLE_STUDENT")) {
            sList = taskStudentRepository.getCourseListOfStudent(CommonMethod.getUsername());
        }else {
            sList = courseRepository.findAll();  //数据库查询操作
        }
        OptionItem item;
        if(sList != null) {
            for (Course c : sList) {
                itemList.add(new OptionItem(c.getCourseId(), c.getCourseId() + "", c.getNum() + "-" + c.getName()));
            }
        }
        return new OptionItemList(0, itemList);
    }

    @PostMapping("/getMaterialTreeNodeList")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public List<MyTreeNode> getMaterialTreeNodeList(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = (Integer)dataRequest.get("courseId");
        List<MyTreeNode> list = new ArrayList<>();
        List<Material> dirList = materialRepository.findByPidNull(courseId);
        if (dirList == null)
            return list;
        MyTreeNode sNode;
        List<MyTreeNode> sList;
        String str;
        List<Material> fileList;
        for (Material mf : dirList) {
            sNode = new MyTreeNode(mf.getId(), mf.getName(), mf.getTitle(), mf.getIsLeaf());
            sList = new ArrayList<MyTreeNode>();
            sNode.setChildren(sList);
            if (mf.getIsLeaf().equals(0)) {
                fileList = materialRepository.findByPidAndCourseId(mf.getId(),courseId);
                for (Material f : fileList) {
                    sList.add(new MyTreeNode(f.getId(), f.getName(), f.getTitle(), f.getIsLeaf()));
                }
            }
            list.add(sNode);
        }
        return list;
    }

    @PostMapping("/materialDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse materialDictionary(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        int count = materialRepository.countMaterialByPid(id);
        if(count > 0) {
            return CommonMethod.getReturnMessageError("存在文件不能删除目录！");
        }
        Optional<Material> op = materialRepository.findById(id);
        if (op.isPresent()) {
            materialRepository.delete(op.get());
        }
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 保存字典信息
     * 前台请求参数 id 要修改菜单的主键 id  value 地点值  label 字典名
     * 返回前端 操作正常
     *
     * @return
     */

    @PostMapping("/materialSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse materialSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Map node = (Map)dataRequest.getMap("node");
        Integer id = CommonMethod.getInteger(node,"id");
        Integer pid = CommonMethod.getInteger(node,"pid");
        String value = CommonMethod.getString(node,"value");
        String title = CommonMethod.getString(node,"title");
        Integer isLeaf = CommonMethod.getInteger(node,"isLeaf");
        Material m = null;
        if(id != null) {
            Optional<Material> op = materialRepository.findById(id);
            if (op.isPresent()) {
                m = op.get();
            }
        }
        if(m == null) {
            m = new Material();
            m.setCourseId(courseId);
            m.setPid(pid);
        }
        m.setTitle(title);
        m.setName(value);
        m.setIsLeaf(isLeaf);
        materialRepository.save(m);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/downloadMaterialFileWeb")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') ")
    public ResponseEntity<StreamingResponseBody> downloadMaterialFileWeb(@Valid @RequestBody DataRequest dataRequest) {
        try {
            Integer materialId = dataRequest.getInteger("materialId");
            Optional<Material> op = materialRepository.findById(materialId);
            if(!op.isPresent())
                return ResponseEntity.internalServerError().build();
            Material m = op.get();
            Course c = courseRepository.getById(m.getCourseId());
            String coursePath = c.getCoursePath();
            Integer pid = m.getPid();
            Material pm= null;
            File file;
            if(pid != null && pid > 0)
                pm = materialRepository.findById(pid).get();
            if(pm != null)
                 file = new File(attachFolder + coursePath+ "/material/"+pm.getName().trim() +"/"+ m.getName().trim());
            else
                file = new File(attachFolder + coursePath+ "/material/"+ m.getName().trim());
            if (file.exists()) {
                int size = (int) file.length();
                byte data[] = new byte[size];
                FileInputStream in = new FileInputStream(file);
                in.read(data);
                in.close();
                MediaType mType = null;
                StreamingResponseBody stream = outputStream -> {
                    outputStream.write(data);
                };
                return ResponseEntity.ok()
                        .contentType(mType)
                        .body(stream);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

}

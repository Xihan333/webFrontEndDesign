package org.fatmansoft.teach.service.student;

import org.fatmansoft.teach.models.student.Achievement;
import org.fatmansoft.teach.models.student.Blog;
import org.fatmansoft.teach.models.student.Blog;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.models.system.User;
import org.fatmansoft.teach.models.teacher.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.BlogRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.repository.system.PersonRepository;
import org.fatmansoft.teach.repository.system.UserRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlogService {

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入

    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入

    @Autowired
    private UserRepository userRepository;  //学生用户数据操作自动注入

    @Autowired
    private BlogRepository blogRepository;

    public synchronized Integer getNewBlogId(){
        Integer id = blogRepository.getMaxId();
        if(id == null){
            id = 1;
        }else{
            id = id+1;
        }
        return id;
    }

    public DataResponse getBlogList() {
        List dataList = getBlogMapList();
        return CommonMethod.getReturnData(dataList);
    }

    public Map getMapFromBlog(Blog blog) {
        Map m = new HashMap();
        Student s;
        Teacher t;
        if (blog == null)
            return m;
        m.put("BlogId",blog.getBlogId());
        m.put("BlogTitle", blog.getTitle());
        m.put("BlogTag", blog.getTag());
        m.put("createTime", blog.getCreateTime());
        m.put("updateTime", blog.getUpdateTime());
        m.put("content", blog.getContent());
        s = blog.getStudent();
        if (s == null)
            return m;
        if (s != null) {
            m.put("personId", s.getPerson().getPersonId());
            m.put("num", s.getPerson().getNum());
            m.put("name", s.getPerson().getName());
        }
        return m;
    }

    public List getBlogMapList() {
        List dataList = new ArrayList();
        List<Blog> sList = blogRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromBlog(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse blogEditSave(DataRequest dataRequest) {
        Integer blogId = dataRequest.getInteger("blogId");  //获取blog_id值
        Map blog = dataRequest.getMap("blog");
        String title = CommonMethod.getString(blog,"title");
        String tag = CommonMethod.getString(blog,"tag");
        String content = CommonMethod.getString(blog,"content");
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        Blog a = null;
        Optional<Blog> op;
        if(blogId != null) {
            op= blogRepository.findByBlogId(blogId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
                if(a.getStudent().getStudentId() != student.getStudentId()){
                    return CommonMethod.getReturnMessageError("修改失败，无法修改他人的博客！");
                }
            }
        }
        if(a == null){
            blogId = getNewBlogId(); //获取Blog新的主键
            a = new Blog();
            a.setBlogId(blogId);
            a.setCreateTime(DateTimeTool.parseDateTime(new Date()));
        }else{
            a.setUpdateTime(DateTimeTool.parseDateTime(new Date()));
        }
        a.setTag(tag);
        a.setTitle(title);
        a.setContent(content);
        a.setStudent(student);
        blogRepository.saveAndFlush(a);//插入新的Blog记录
        return CommonMethod.getReturnData(a.getBlogId(),"修改或新增成功");  // 将BlogId返回前端
    }

    public DataResponse getMyBlogList() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer studentId = s.getStudentId();
        List dataList = getBlogMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    private List getBlogMapListByStudentId(Integer studentId) {
        List dataList = new ArrayList();
        List<Blog> sList = blogRepository.findBlogByStudentId(studentId);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromBlog(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse blogDelete(DataRequest dataRequest) {
        Integer blogId = dataRequest.getInteger("blogId");  //获取blog_id值
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student student= sOp.get();
        Integer studentId = student.getStudentId();
        Blog a = null;
        Optional<Blog> op;
        if(blogId != null) {
            op= blogRepository.findByBlogId(blogId);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
                if(a.getStudent().getStudentId() != student.getStudentId()){
                    return CommonMethod.getReturnMessageError("删除失败，无法删除他人的博客！");
                }
            }
        }
        if(a != null) {
            blogRepository.delete(a);//删除该条成就
        }
        return CommonMethod.getReturnMessageOK("删除成功");  //通知前端操作正常
    }

    public DataResponse getOneBlogList(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        List dataList = getBlogMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getMyBlogNumber() {
        Integer userId = CommonMethod.getUserId();
        Optional<User> uOp = userRepository.findByUserId(userId);  // 查询获得 user对象
        if(!uOp.isPresent())
            return CommonMethod.getReturnMessageError("用户不存在！");
        User u = uOp.get();
        Optional<Student> sOp= studentRepository.findByPersonPersonId(u.getUserId());  // 查询获得 Student对象
        if(!sOp.isPresent())
            return CommonMethod.getReturnMessageError("学生不存在！");
        Student s= sOp.get();
        Integer studentId = s.getStudentId();
        List dataList = getBlogMapListByStudentId(studentId);
        return CommonMethod.getReturnData(dataList.size());
    }
}

package org.fatmansoft.teach.controllers.student;

import org.fatmansoft.teach.models.student.Achievement;
import org.fatmansoft.teach.models.student.Blog;
import org.fatmansoft.teach.models.student.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.student.BlogRepository;
import org.fatmansoft.teach.repository.student.StudentRepository;
import org.fatmansoft.teach.service.student.BlogService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/blog")
public class BlogController {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BlogService blogService;

    /**
     * 获取全部的博客
     */
    @GetMapping("/getBlogList")
    public DataResponse getBlogList() {
        return blogService.getBlogList();
    }

    /**
     * 新增或编辑博客
     * @param dataRequest
     * @return
     */
    @PostMapping("/blogEditSave")
    public DataResponse BlogEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return blogService.blogEditSave(dataRequest);
    }

    /**
     * 获取当前用户的博客
     * @return
     */
    @GetMapping("/getMyBlogList")
    public DataResponse getMyBlogList() {
        return blogService.getMyBlogList();
    }

    /**
     * 删除博客
     * @param dataRequest
     * @return
     */
    @PostMapping("/blogDelete")
    public DataResponse blogDelete(@Valid @RequestBody DataRequest dataRequest) {
        return blogService.blogDelete(dataRequest);
    }

    /**
     * 获得某个人的博客
     * @param dataRequest
     * @return
     */
    @PostMapping("/getOneBlogList")
    public DataResponse getOneBlogList(@Valid @RequestBody DataRequest dataRequest){
        return blogService.getOneBlogList(dataRequest);
    }

}

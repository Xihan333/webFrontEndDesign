package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Blog;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer> {
    @Query(value = "select max(id) from Blog  ")
    Integer getMaxId();


    @Query(value = "from Blog where ?1='' or title like %?1% ")
    List<Blog> findBlogListByTitle(String title);

    @Query(value = "from Blog where student.studentNum=?1")
    List<Blog> findBlogListByStudentNum(String studentNum);

    @Query(value = "from Blog where student.id=?1")
    List<Blog> findBlogListByStudentId(Integer studentId);
}

package org.fatmansoft.teach.repository.student;


import org.fatmansoft.teach.models.student.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer> {

    @Query(value = "select max(blogId) from Blog")
    Integer getMaxId();

    @Query(value= "from Blog where blogId= ?1")
    Optional<Blog> findByBlogId(Integer blogId);

    @Query(value= "from Blog where student.studentId= ?1")
    List<Blog> findBlogByStudentId(Integer studentId);
}

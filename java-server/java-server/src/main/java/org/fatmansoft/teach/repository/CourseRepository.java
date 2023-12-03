package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "select max(id) from Course  ")
    Integer getMaxId();

    Optional<Course> findByCourseNum(String courseNum);

    Optional<Course> findById(Integer courseId);

    @Query(value = "from Course where ?1='' or courseNum like %?1% or courseName like %?1% ")
    List<Course> findCourseListByNumName(String numName);

    @Query(value = "from Course where teacher.id=?1")
    List<Course> findCourseListByTeacherId(Integer teacherId);

    @Query(value = "from Course where sessions.id=?1")
    List<Course> findCourseListBySessionsId(Integer sessionsId);
}

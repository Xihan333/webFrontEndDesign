package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    @Query(value = "select max(teacherId) from Teacher  ")
    Integer getMaxId();

    @Query(value = "select t from User u,Teacher t where u.userId=?1 and u.person.personId=t.person.personId")
    Optional<Teacher> findByUserId(Integer userId);

    Optional<Teacher> findByPersonPersonId(Integer personId);
    Optional<Teacher> findByPersonNum(String num);
    Optional<Teacher> findByPersonName(String name);

    @Query(value = "select tc.teacher from TeacherCourse tc where tc.course.courseId=?1")
    List<Teacher> findTeacherListByCourseCourseId(Integer courseId);

    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);

}

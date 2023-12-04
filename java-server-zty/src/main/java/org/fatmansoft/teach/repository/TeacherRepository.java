package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    Optional<Teacher> findByTeacherNum(String teacherNum);
    List<Teacher> findByTeacherName(String teacherName);

    @Query(value = "select max(id) from Teacher  ")
    Integer getMaxId();

    @Query(value = "from Teacher where ?1='' or teacherNum like %?1% or teacherName like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);

    @Query(value = "select * from teacher  where ?1='' or teacher_num like %?1% or teacher_name like %?1% ", nativeQuery = true)
    List<Teacher> findTeacherListByNumNameNative(String numName);

}
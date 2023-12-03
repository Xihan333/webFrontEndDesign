package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Award;
import org.fatmansoft.teach.models.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper,Integer> {
    @Query(value = "select max(id) from Paper  ")
    Integer getMaxId();

//    Optional<Paper> findByPaperName(String PaperName);

    @Query(value = "from Paper where ?1='' or PaperName like %?1% or teacher.teacherNum like %?1% ")
    List<Paper> findPaperListByName(String name);

    @Query(value = "from Paper where teacher.teacherNum=?2 and (?1='' or PaperName like %?1% or teacher.teacherNum like %?1%) ")
    List<Paper> findPaperListByNameForTeacher(String name, String teacherNum);
    @Query(value = "from Paper where teacher.id=?1")
    List<Paper> findPaperListByTeacherId(Integer teacherId);


}

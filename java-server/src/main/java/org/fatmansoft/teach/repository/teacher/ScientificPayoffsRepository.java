package org.fatmansoft.teach.repository.teacher;


import org.fatmansoft.teach.models.teacher.ScientificPayoffs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScientificPayoffsRepository extends JpaRepository<ScientificPayoffs,Integer> {
    @Query(value = "select max(scientificPayoffsId) from ScientificPayoffs ")
    Integer getMaxId();

    @Query(value= "from ScientificPayoffs where teacher.teacherId= ?1")
    List<ScientificPayoffs> findScientificPayoffsByTeacherId(Integer teacherId);

    Optional<ScientificPayoffs> findByScientificPayoffsId(Integer scientificPayoffsId);

    @Query(value = "from ScientificPayoffs where ?1='' or  teacher.person.num like %?1% or teacher.person.name like %?1% ")
    List<ScientificPayoffs> findScientificPayoffsListByTeacherNumName(String numName);

}

package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Campus;
import org.fatmansoft.teach.models.student.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<Campus,Integer>{

    @Query(value = "select max(campusId) from Campus")
    Integer getMaxId();

    @Query(value = "from Campus where campusId=?1")
    Optional<Campus> findByCampusId(Integer campusId);

}

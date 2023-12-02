package org.fatmansoft.teach.repository.studentInfo;

import org.fatmansoft.teach.models.studentInfo.Punishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PunishmentRepository extends JpaRepository<Punishment,Integer> {
    @Query(value = "select max(punishmentId) from Punishment")
    Integer getMaxId();

    @Query(value= "from Punishment where student.studentId= ?1")
    List<Punishment> findPunishmentByStudentId(Integer studentId);

    Optional<Punishment> findByPunishmentId(Integer punishmentId);

    @Query(value = "from Punishment where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<Punishment> findPunishmentListByNumName(String numName);
}

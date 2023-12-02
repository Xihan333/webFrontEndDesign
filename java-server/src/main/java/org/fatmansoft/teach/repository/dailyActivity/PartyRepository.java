package org.fatmansoft.teach.repository.dailyActivity;

import org.fatmansoft.teach.models.dailyActivity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party,Integer> {
    @Query(value = "select max(partyId) from Party")
    Integer getMaxId();

    Optional<Party> findByPartyId(Integer partyId);
    @Query(value= "from Party where student.studentId= ?1")
    List<Party> findPartyByStudentId(Integer studentId);

    @Query(value = "from Party where ?1='' or organizer like %?1% or location like %?1% ")
    List<Party> findPartyListByNumName(String organizerLocation);
}

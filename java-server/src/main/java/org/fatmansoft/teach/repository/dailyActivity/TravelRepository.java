package org.fatmansoft.teach.repository.dailyActivity;

import org.fatmansoft.teach.models.dailyActivity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelRepository extends JpaRepository<Travel,Integer> {
    @Query(value = "select max(travelId) from Travel")
    Integer getMaxId();

    @Query(value= "from Travel where student.studentId= ?1")
    List<Travel> findTravelByStudentId(Integer studentId);

    Optional<Travel> findByTravelId(Integer travelId);

    @Query(value = "from Travel where ?1='' or day like %?1% or location like %?1% ")
    List<Travel> findTravelListByNumName(String dayLocation);

}

package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialPracticeRepository extends JpaRepository<SocialPractice,Integer> {
    @Query(value = "select max(id) from SocialPractice  ")
    Integer getMaxId();

//    Optional<Award> findByAwardName(String AwardName);

    @Query(value = "from SocialPractice where ?1='' or socialPracticeName like %?1% or student.studentName like %?1% ")
    List<SocialPractice> findSocialPracticeListByName(String name);
    @Query(value = "from SocialPractice where student.studentNum=?2 and (?1='' or socialPracticeName like %?1% or student.studentName like %?1%) ")
    List<SocialPractice> findSocialPracticeListByNameForStudent(String name, String studentNum);
    @Query(value = "from SocialPractice where student.id=?1")
    List<SocialPractice> findSocialPracticeListByStudentId(Integer studentId);


}

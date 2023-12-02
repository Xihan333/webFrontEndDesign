package org.fatmansoft.teach.repository.creativePratice;

import org.fatmansoft.teach.models.creativePractice.Social;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialRepository extends JpaRepository<Social,Integer> {
    @Query(value = "select max(socialId) from Social ")
    Integer getMaxId();

    @Query(value= "from Social where student.studentId= ?1")
    List<Social> findSocialByStudentId(Integer studentId);

    Optional<Social> findBySocialId(Integer socialId);

    @Query(value = "from Social where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<Social> findSocialListByNumName(String numName);

    @Query(value= "from Social where auditStatus=0")
    List<Social> findWaitingSocialList();

    @Query(value= "from Social where auditStatus=1")
    List<Social> findPassedSocialList();

    @Query(value= "from Social where auditStatus=2")
    List<Social> findUnpassedSocialList();

    @Query(value= "from Social where auditStatus=0")
    List<Social> findAllSocialList();
}

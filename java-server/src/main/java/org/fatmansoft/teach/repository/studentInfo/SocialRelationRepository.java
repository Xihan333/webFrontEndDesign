package org.fatmansoft.teach.repository.studentInfo;

import org.fatmansoft.teach.models.studentInfo.SocialRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialRelationRepository extends JpaRepository<SocialRelation,Integer> {
    @Query(value = "select max(socialRelationId) from SocialRelation ")
    Integer getMaxId();

    @Query(value = "from SocialRelation where ?1= student.studentId ")
    List<SocialRelation> findSocialRelationListByStudentId(Integer studentId);

    @Query(value = "from SocialRelation where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<SocialRelation> findSocialRelationListByNumName(String numName);

    @Query(value = "from SocialRelation where ?1= teacher.teacherId ")
    List<SocialRelation> findSocialRelationListByTeacherId(Integer teacherId);
}

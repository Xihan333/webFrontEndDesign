package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTagRepository extends JpaRepository<UserTag,Integer> {
    @Query(value = "select max(id) from UserTag  ")
    Integer getMaxId();

    @Query(value="from UserTag  where tagger.studentNum = ?2 and student.id = ?1 and tag.id = ?3")
    Optional<UserTag> findByStudentTaggerTag(Integer studentId, String taggerNum, Integer tagId);

    @Query(value="from UserTag  where student.id = ?1")
    List<UserTag> findUserTagListByStudentId(Integer studentId);
    @Query(value="from UserTag  where tagger.id = ?1")
    List<UserTag> findUserTagListByTaggerId(Integer taggerId);
    @Query(value="from UserTag  where tag.id = ?1")
    List<UserTag> findUserTagListByTagId(Integer tagId);
}

package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    @Query(value = "select max(id) from Tag  ")
    Integer getMaxId();
    Optional<Tag> findByTag(String tag);
}

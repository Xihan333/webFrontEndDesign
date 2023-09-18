package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material,Integer> {
    @Query(value=" from Material where pid is null and courseId=?1")
    List<Material> findByPidNull(Integer courseId);
    List<Material> findByPidAndCourseId(Integer pid, Integer courseId);

    int countMaterialByPid(Integer pid);
}

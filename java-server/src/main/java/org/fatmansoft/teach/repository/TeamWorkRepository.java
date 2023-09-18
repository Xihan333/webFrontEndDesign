package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.TeamWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamWorkRepository extends JpaRepository<TeamWork,Integer> {
    List<TeamWork> findByStudentStudentIdAndCourseId(Integer studentId, Integer courseId);
}

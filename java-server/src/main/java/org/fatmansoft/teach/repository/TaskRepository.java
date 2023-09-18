package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Integer> {
}

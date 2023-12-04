package org.fatmansoft.teach.repository.student;

import org.fatmansoft.teach.models.student.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Integer> {

    @Query(value = "select max(evaluationId) from Evaluation")
    Integer getMaxId();

    @Query(value= "from Evaluation where evaluationId= ?1")
    Optional<Evaluation> findByEvaluationId(Integer evaluationId);

    @Query(value= "from Evaluation where student.studentId= ?1")
    List<Evaluation> findEvaluationByStudentId(Integer studentId);

    @Query(value= "from Evaluation where evaluator.studentId= ?1")
    List<Evaluation> findEvaluationByEvaluatorId(Integer studentId);
}

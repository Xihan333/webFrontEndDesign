package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Integer> {
    @Query(value = "select max(id) from Evaluation  ")
    Integer getMaxId();

    List<Evaluation> findByStudentId(Integer studentId);

//    @Query(value="from Evaluation  where evaluator.id = ?1")
//    List<Evaluation> findEvaluationListByEvaluatorId(Integer evaluatorId);

    @Query(value="from Evaluation  where ?1='' or student.studentNum like %?1% or student.studentName like %?1% or evaluator.studentNum like %?1% or evaluator.studentName like %?1%")
    List<Evaluation> findEvaluationListByInput(String input);

    @Query(value="from Evaluation  where evaluator.studentNum = ?1")
    List<Evaluation> findEvaluationListByEvaluatorNum(String evaluatorNum);

    @Query(value="from Evaluation  where evaluator.id = ?1")
    List<Evaluation> findEvaluationListByEvaluatorId(Integer evaluatorId);

    @Query(value="from Evaluation  where student.studentNum = ?1")
    List<Evaluation> findEvaluationListByStudentNum(String studentNum);
}

package org.fatmansoft.teach.repository.creativePratice;

import org.fatmansoft.teach.models.creativePractice.TrainingLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingLectureRepository extends JpaRepository<TrainingLecture,Integer> {
    @Query(value = "select max(trainingLectureId) from TrainingLecture ")
    Integer getMaxId();

    @Query(value= "from TrainingLecture where student.studentId= ?1")
    List<TrainingLecture> findTrainingLectureByStudentId(Integer studentId);

    @Query(value= "from TrainingLecture where teacher.teacherId= ?1")
    List<TrainingLecture> findTrainingLectureByTeacherId(Integer teacherId);

    Optional<TrainingLecture> findByTrainingLectureId(Integer trainingLectureId);

    @Query(value = "from TrainingLecture where ?1='' or  teacher.person.num like %?1% or teacher.person.name like %?1% ")
    List<TrainingLecture> findTrainingLectureListByTeacherNumName(String numName);
    @Query(value = "from TrainingLecture where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<TrainingLecture> findTrainingLectureListByStudentNumName(String numName);

    @Query(value= "from TrainingLecture where auditStatus=0")
    List<TrainingLecture> findWaitingTrainingLectureList();

    @Query(value= "from TrainingLecture where auditStatus=1")
    List<TrainingLecture> findPassedTrainingLectureList();

    @Query(value = "from TrainingLecture where auditStatus=2")
    List<TrainingLecture> findFailedTrainingLectureList();

    @Query(value= "from TrainingLecture where auditStatus=0")
    List<TrainingLecture> findAllTrainingLectureList();

}

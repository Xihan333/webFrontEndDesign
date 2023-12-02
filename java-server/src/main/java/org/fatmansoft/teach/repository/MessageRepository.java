package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Message;
import org.fatmansoft.teach.models.studentInfo.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
    public interface MessageRepository extends JpaRepository<Message,Integer> {

        @Query(value = "select max(messageId) from Message  ")
        Integer getMaxId();
        Optional<Message> findById(Integer messageId);

        @Query(value= "from Message where student.studentId= ?1")
        List<Message> findMessageByStudentId(Integer studentId);

        @Query(value = "from Message where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
        List<Message> findMessageListByNumName(String numName);

        @Query(value= "from Message where status=0")
        List<Message> findWaitingMessageList();

        @Query(value= "from Message where status=1")
        List<Message> findPassedMessageList();

        @Query(value= "from Message where status=2")
        List<Message> findFailedMessageList();
}



package org.fatmansoft.teach.repository;


import java.util.List;
import java.util.Optional;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByPersonPerNum(String perNum);
    Optional<User> findByUserId(Integer userId);
    @Query(value = "select max(id) from User  ")
    Integer getMaxId();
    Boolean existsByUserName(String userName);

    @Query(value = "from User where ?1='' or userName like %?1% ")
    List<User> findUserListByUserName(String userName);
    @Query(value = "from User where userName=?1 ")
    Optional<User> findUserListByUserName2(String userName);
    @Query(value = "from User where student.id=?1 ")
    List<User> findUserListByStudentId(Integer studentId);

    @Query(value = "from User where teacher.id=?1 ")
    List<User> findUserListByTeacherId(Integer teacherId);
}
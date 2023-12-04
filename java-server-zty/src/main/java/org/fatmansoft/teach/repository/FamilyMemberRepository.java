package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
    @Query(value = "select max(id) from FamilyMember  ")
    Integer getMaxId();
    List<FamilyMember> findByStudentId(Integer studentId);
    @Query(value="from FamilyMember where student.id=?1")
    List<FamilyMember> findByStudent(Integer studentId);
}

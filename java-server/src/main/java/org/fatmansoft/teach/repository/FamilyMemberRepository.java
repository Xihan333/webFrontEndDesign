package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
    List<FamilyMember> findByStudentStudentId(Integer studentId);
}

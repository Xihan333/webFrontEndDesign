package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InfoRepository extends JpaRepository<Info, Integer> {
    List<Info> findByInfo(String info);
    @Query(value=" from Info where info=?1")
    List<Info> findByInfoTest(String info);
    @Query(value=" select * from info where info=?1",nativeQuery = true)
    List<Info> findByInfoTest1(String info);
}

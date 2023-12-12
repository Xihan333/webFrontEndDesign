package org.fatmansoft.teach.repository.system;

import org.fatmansoft.teach.models.system.StaticValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticValueRepository extends JpaRepository<StaticValue, String> {
    String findByKey(String key);
}

package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog,Integer> {
}

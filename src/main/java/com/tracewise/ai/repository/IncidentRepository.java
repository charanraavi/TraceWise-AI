package com.tracewise.ai.repository;

import com.tracewise.ai.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByStatus(String status);

    List<Incident> findByStatusCode(Integer statusCode);
}

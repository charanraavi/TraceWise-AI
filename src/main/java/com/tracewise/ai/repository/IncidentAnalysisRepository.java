
package com.tracewise.ai.repository;

import com.tracewise.ai.entity.IncidentAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface IncidentAnalysisRepository
        extends JpaRepository<IncidentAnalysis, Long> {

    List<IncidentAnalysis> findByIncident_IdOrderByCreatedAtDesc(Long incidentId);

    Page<IncidentAnalysis> findByIncident_Id(
            Long incidentId,
            Pageable pageable
    );

}
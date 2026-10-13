
package com.tracewise.ai.service;

import com.tracewise.ai.entity.Incident;
import com.tracewise.ai.entity.IncidentAnalysis;
import com.tracewise.ai.repository.IncidentAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentAnalysisRepository incidentAnalysisRepository;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void getAnalysisHistoryShouldReturnRepositoryResults() {
        Incident incident = new Incident();
        incident.setId(3L);
        IncidentAnalysis analysis = new IncidentAnalysis();
        analysis.setIncident(incident);
        analysis.setAnalysisText("Check database connectivity");

        when(incidentAnalysisRepository
                .findByIncident_IdOrderByCreatedAtDesc(3L))
                .thenReturn(java.util.List.of(analysis));

        var result = incidentService.getAnalysisHistory(3L);

        assertEquals(1, result.size());
        assertEquals(
                "Check database connectivity",
                result.get(0).getAnalysisText()
        );

        verify(incidentAnalysisRepository)
                .findByIncident_IdOrderByCreatedAtDesc(3L);
    }
}

package com.tracewise.ai.dto;

import java.time.LocalDateTime;

public class IncidentAnalysisResponse {

    private Long id;
    private Long incidentId;
    private String analysisText;
    private LocalDateTime createdAt;

    public IncidentAnalysisResponse() {
    }

    public IncidentAnalysisResponse(
            Long id,
            Long incidentId,
            String analysisText,
            LocalDateTime createdAt) {
        this.id = id;
        this.incidentId = incidentId;
        this.analysisText = analysisText;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public String getAnalysisText() {
        return analysisText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
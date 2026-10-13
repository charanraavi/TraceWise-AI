package com.tracewise.ai.service;


import com.tracewise.ai.dto.IncidentAnalysisResponse;
import com.tracewise.ai.entity.Incident;
import com.tracewise.ai.entity.IncidentAnalysis;
import com.tracewise.ai.exception.IncidentNotFoundException;
import com.tracewise.ai.repository.IncidentAnalysisRepository;
import com.tracewise.ai.repository.IncidentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.tracewise.ai.dto.IncidentResponse;


@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentAnalysisRepository incidentAnalysisRepository;

    public IncidentService(IncidentRepository incidentRepository, IncidentAnalysisRepository incidentAnalysisRepository) {

        this.incidentRepository = incidentRepository;
        this.incidentAnalysisRepository = incidentAnalysisRepository;
    }
     public Page<Incident> getAllIncidents(int pageNumber, int pageSize, String sortBy, String sortDirection){

         Sort sort = sortDirection.equalsIgnoreCase("desc")
                 ?Sort.by(sortBy).descending()
                 :Sort.by(sortBy).ascending();
         Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

         return incidentRepository.findAll(pageable);
     }

    public Incident saveIncident(Incident incident){

        incident.setStatus("OPEN");

        incident.setCreatedAt(LocalDateTime.now());

        return incidentRepository.save(incident);
    }
//fetch by id
    public Incident getIncidentById(Long id){

        Optional<Incident> incident =
                incidentRepository.findById(id);

        if (incident.isPresent()) {
            return incident.get();
        }

        throw new IncidentNotFoundException(
                "Incident not found with ID: " + id
        );
    }
//updating incidents
public Incident updateIncident(Long id, Incident updatedIncident) {

    Incident incident = incidentRepository.findById(id)
            .orElseThrow(() ->
                    new IncidentNotFoundException(
                            "Incident not found with ID: " + id
                    )
            );

    incident.setEndpoint(updatedIncident.getEndpoint());
    incident.setStatusCode(updatedIncident.getStatusCode());
    incident.setErrorMessage(updatedIncident.getErrorMessage());
    incident.setStackTrace(updatedIncident.getStackTrace());
    incident.setAiAnalysis(updatedIncident.getAiAnalysis());
    incident.setStatus(updatedIncident.getStatus());

    return incidentRepository.save(incident);
}

    public void deleteIncident(Long id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found with ID: " + id
                        )
                );

        incidentRepository.delete(incident);
    }


    public IncidentResponse convertToResponse(Incident incident) {

        IncidentResponse response = new IncidentResponse();

        response.setId(incident.getId());
        response.setEndpoint(incident.getEndpoint());
        response.setStatusCode(incident.getStatusCode());
        response.setErrorMessage(incident.getErrorMessage());
        response.setStackTrace(incident.getStackTrace());
        response.setAiAnalysis(incident.getAiAnalysis());
        response.setStatus(incident.getStatus());
        response.setCreatedAt(incident.getCreatedAt());

        return response;
    }


    public List<Incident> getIncidentsByStatus(String status) {
        return incidentRepository.findByStatus(status);
    }

    public List<Incident> getIncidentsByStatusCode(Integer statusCode) {
        return incidentRepository.findByStatusCode(statusCode);
    }

    public IncidentAnalysis saveAnalysisHistory(
            Incident incident, String aiResponse) {

        IncidentAnalysis history = new IncidentAnalysis();
        history.setIncident(incident);
        history.setAnalysisText(aiResponse);

        return incidentAnalysisRepository.save(history);
    }

    public List<IncidentAnalysisResponse> getAnalysisHistory(Long incidentId) {

        List<IncidentAnalysis> history =
                incidentAnalysisRepository
                        .findByIncident_IdOrderByCreatedAtDesc(incidentId);

        return history.stream()
                .map(analysis -> new IncidentAnalysisResponse(
                        analysis.getId(),
                        analysis.getIncident().getId(),
                        analysis.getAnalysisText(),
                        analysis.getCreatedAt()
                ))
                .toList();
    }

    public Page<IncidentAnalysisResponse> getAnalysisHistory(
            Long incidentId, Pageable pageable) {

        return incidentAnalysisRepository
                .findByIncident_Id(incidentId, pageable)
                .map(analysis -> new IncidentAnalysisResponse(
                        analysis.getId(),
                        analysis.getIncident().getId(),
                        analysis.getAnalysisText(),
                        analysis.getCreatedAt()
                ));
    }

}

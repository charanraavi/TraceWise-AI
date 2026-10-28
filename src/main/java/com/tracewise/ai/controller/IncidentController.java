package com.tracewise.ai.controller;

import com.tracewise.ai.dto.IncidentAnalysisResponse;
import com.tracewise.ai.dto.IncidentResponse;
import com.tracewise.ai.entity.Incident;
import com.tracewise.ai.entity.IncidentAnalysis;
import com.tracewise.ai.exception.IncidentNotFoundException;
import com.tracewise.ai.response.ResponceStructure;
import com.tracewise.ai.service.IncidentService;
import com.tracewise.ai.service.OllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tracewise.ai.dto.IncidentRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final OllamaService ollamaService;


    public IncidentController(
            IncidentService incidentService,
            OllamaService ollamaService) {

        this.incidentService = incidentService;
        this.ollamaService = ollamaService;
    }
    @Operation(
            summary = "Create Incident",
            description = "Creates a new incident using the incident details provided in the request body."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Incident created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid incident details"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @PostMapping
    public ResponseEntity<ResponceStructure<IncidentResponse>> createIncident(
            @Valid @RequestBody IncidentRequest request) {

        Incident incident = new Incident();
        incident.setEndpoint(request.getEndpoint());
        incident.setStatusCode(request.getStatusCode());
        incident.setErrorMessage(request.getErrorMessage());
        incident.setStackTrace(request.getStackTrace());

        Incident savedIncident = incidentService.saveIncident(incident);

        IncidentResponse incidentResponse =
                incidentService.convertToResponse(savedIncident);

        ResponceStructure<IncidentResponse> response =
                new ResponceStructure<>(
                        HttpStatus.CREATED.value(),
                        "Incident created successfully",
                        incidentResponse
                );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Incidents",
            description = "Retrieves all incidents with support for pagination and sorting."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidents retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @GetMapping
    public ResponseEntity<ResponceStructure<Page<IncidentResponse>>> getAllIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<Incident> incidents = incidentService.getAllIncidents(
                page,
                size,
                sortBy,
                sortDirection
        );

        Page<IncidentResponse> incidentResponses =
                incidents.map(incidentService::convertToResponse);

        ResponceStructure<Page<IncidentResponse>> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "Incidents retrieved successfully",
                        incidentResponses
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Get Incident By ID",
            description = "Retrieve an incident using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponceStructure<IncidentResponse>> getIncidentById(@Parameter(
            description = "Unique ID of the incident",
            required = true
    )
            @PathVariable Long id) {

        Incident incident = incidentService.getIncidentById(id);

        IncidentResponse incidentResponse =
                incidentService.convertToResponse(incident);

        ResponceStructure<IncidentResponse> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "Incident retrieved successfully",
                        incidentResponse
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//updating based on id

    @Operation(
            summary = "Update Incident",
            description = "Updates an existing incident using its ID and the provided incident details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid incident details"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponceStructure<IncidentResponse>> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody IncidentRequest request) {

        Incident updatedIncident = new Incident();
        updatedIncident.setEndpoint(request.getEndpoint());
        updatedIncident.setStatusCode(request.getStatusCode());
        updatedIncident.setErrorMessage(request.getErrorMessage());
        updatedIncident.setStackTrace(request.getStackTrace());

        Incident incident = incidentService.updateIncident(id, updatedIncident);

        IncidentResponse incidentResponse =
                incidentService.convertToResponse(incident);

        ResponceStructure<IncidentResponse> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "Incident updated successfully",
                        incidentResponse
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //deleting incident by id
    @Operation(
            summary = "Delete Incident",
            description = "Deletes an existing incident using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponceStructure<String>> deleteIncident(
            @PathVariable Long id) {

        incidentService.deleteIncident(id);
        ResponceStructure<String> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "Incident deleted successfully",
                        "Deleted Incident ID: " + id
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Get Incidents By Status",
            description = "Retrieves all incidents matching the specified status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidents retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid incident status"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<ResponceStructure<List<IncidentResponse>>> getByStatus(
            @Parameter(
                    description = "Status used to filter incidents",
                    required = true
            )
            @PathVariable String status) {

        List<IncidentResponse> incidents = incidentService
                .getIncidentsByStatus(status)
                .stream()
                .map(incidentService::convertToResponse)
                .toList();

        ResponceStructure<List<IncidentResponse>> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "Incidents filtered by status",
                        incidents
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Get Incidents By Status Code",
            description = "Retrieves incidents matching the specified HTTP or application status code."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidents retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status code"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @GetMapping("/status-code/{statusCode}")
    public ResponseEntity<ResponceStructure<List<IncidentResponse>>> getByStatusCode(
            @Parameter(
                    description = "Status code used to filter incidents",
                    required = true
            )
            @PathVariable int statusCode) {

        List<IncidentResponse> incidents = incidentService
                .getIncidentsByStatusCode(statusCode)
                .stream()
                .map(incidentService::convertToResponse)
                .toList();

        ResponceStructure<List<IncidentResponse>> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "Incidents filtered by HTTP status code",
                        incidents
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Analyze Incident Using AI",
            description = "Analyzes an incident and provides possible root causes and troubleshooting recommendations."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident analyzed successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @PostMapping("/{id}/analyze")
    public ResponseEntity<ResponceStructure<String>> analyzeIncident(
            @PathVariable Long id) {

        // 1. Retrieve the incident from PostgreSQL
        Incident incident = incidentService.getIncidentById(id);

        // 2. Prepare the prompt for Ollama
        String prompt = """
            You are TraceWise AI, an API incident troubleshooting assistant.

            Analyze the following API incident.

            Endpoint: %s
            HTTP Status Code: %s
            Error Message: %s
            Stack Trace: %s

            Provide your analysis in these sections:
            1. Possible Root Cause
            2. Troubleshooting Steps
            3. Recommended Solution
            4. Prevention Tips

            If information is insufficient, clearly state your assumptions.
            Do not claim that a possible cause is confirmed without evidence.
            """.formatted(
                incident.getEndpoint(),
                incident.getStatusCode(),
                incident.getErrorMessage(),
                incident.getStackTrace()
        );

        // 3. Generate the AI response
        String aiResponse = ollamaService.generateResponse(prompt);

        incident.setAiAnalysis(aiResponse);
        incidentService.updateIncident(incident.getId(), incident);

        incidentService.saveAnalysisHistory(incident, aiResponse);

        // 4. Return the result
        ResponceStructure<String> response =
                new ResponceStructure<>(
                        HttpStatus.OK.value(),
                        "AI incident analysis completed successfully",
                        aiResponse
                );

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get Incident Analysis History",
            description = "Retrieves the previously generated AI analysis records for a specific incident."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Analysis history retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @GetMapping("/{id}/analysis-history")
    public ResponseEntity<List<IncidentAnalysisResponse>> getAnalysisHistory(
            @PathVariable Long id) {

        List<IncidentAnalysisResponse> history =
                incidentService.getAnalysisHistory(id);

        return ResponseEntity.ok(history);
    }

    @Operation(
            summary = "Get Paginated Analysis History",
            description = "Retrieves incident analysis history page by page, with support for pagination and sorting."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated analysis history retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @GetMapping("/{id}/analysis-history/page")
    public ResponseEntity<Page<IncidentAnalysisResponse>> getAnalysisHistoryPage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(
                page,
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                incidentService.getAnalysisHistory(id, pageable)
        );
    }
}

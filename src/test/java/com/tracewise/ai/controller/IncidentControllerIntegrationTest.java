
package com.tracewise.ai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void getIncidentsWithoutTokenShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getIncidentsWithInvalidTokenShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(get("/api/incidents")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void developerCannotDeleteIncident() throws Exception {
        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "INCIDENT_ANALYST")
    void analystCannotDeleteIncident() throws Exception {
        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isForbidden());
    }


    private String generateToken(String role) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tracewise-ai")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(600))
                .subject("integration-test-user")
                .claim("role", role)
                .build();

        JwsHeader header = JwsHeader.with(
                org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256
        ).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    @Test
    void platformAdminCanDeleteIncident() throws Exception {
        String token = generateToken("PLATFORM_ADMIN");

        String requestBody = """
            {
                "endpoint": "/api/test-delete",
                "statusCode": 500,
                "errorMessage": "Temporary test incident",
                "stackTrace": "Integration test"
            }
            """;

        String response = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/api/incidents")
                                .header("Authorization", "Bearer " + token)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the newly created incident ID from the response.
        long incidentId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .path("data")
                .path("id")
                .asLong();

        mockMvc.perform(delete("/api/incidents/" + incidentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
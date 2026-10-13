package com.tracewise.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IncidentRequest {
        @NotBlank(message = "Endpoint must not be blank")
        private String endpoint;

        @NotNull(message = "Status code must not be null")
        private Integer statusCode;

        @NotBlank(message = "Error message must not be blank")
        private String errorMessage;

        @NotBlank(message = "Stack trace must not be blank")
        private String stackTrace;

    public @NotBlank(message = "Endpoint must not be blank") String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(@NotBlank(message = "Endpoint must not be blank") String endpoint) {
        this.endpoint = endpoint;
    }

    public @NotNull(message = "Status code must not be null") Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(@NotNull(message = "Status code must not be null") Integer statusCode) {
        this.statusCode = statusCode;
    }

    public @NotBlank(message = "Error message must not be blank") String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@NotBlank(message = "Error message must not be blank") String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public @NotBlank(message = "Stack trace must not be blank") String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(@NotBlank(message = "Stack trace must not be blank") String stackTrace) {
        this.stackTrace = stackTrace;
    }
}


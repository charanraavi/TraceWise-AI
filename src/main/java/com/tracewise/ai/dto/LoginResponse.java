
package com.tracewise.ai.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String email,
        String role
) {}
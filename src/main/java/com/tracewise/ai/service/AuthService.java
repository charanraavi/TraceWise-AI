
package com.tracewise.ai.service;

import com.tracewise.ai.config.JwtConfig;
import com.tracewise.ai.config.UserRole;
import com.tracewise.ai.dto.LoginRequest;
import com.tracewise.ai.dto.LoginResponse;
import com.tracewise.ai.dto.RegisterRequest;
import com.tracewise.ai.entity.AppUser;
import com.tracewise.ai.repository.AppUserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtEncoder jwtEncoder;

    public AuthService(
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtEncoder jwtEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email().trim().toLowerCase(),
                                request.password()
                        )
                );

        AppUser user = appUserRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid credentials"));

        Instant now = Instant.now();
        Instant expiresAt = now.plus(30, ChronoUnit.MINUTES);

        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("User has no assigned role"));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tracewise-ai")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("role", role)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        org.springframework.security.oauth2.jwt
                                .JwsHeader.with(MacAlgorithm.HS256)
                                .build(),
                        claims
                )
        ).getTokenValue();

        return new LoginResponse(
                token,
                "Bearer",
                1800,
                user.getEmail(),
                role
        );
    }


    @Transactional
    public void register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(
                passwordEncoder.encode(request.password()));
        user.setRole(UserRole.DEVELOPER);
        user.setEnabled(true);

        appUserRepository.save(user);
    }
}
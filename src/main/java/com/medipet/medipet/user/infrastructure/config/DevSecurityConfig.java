package com.medipet.medipet.user.infrastructure.config;

import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@Profile("dev")
class DevSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    JwtAuthenticationFilter jwtFilter,
                                    DaoAuthenticationProvider authProvider,
                                    CorsConfigurationSource corsConfigurationSource) {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Stateless JWT API (Bearer token, no cookies/sessions): CSRF protection is not applicable.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(GET, "/actuator/health").permitAll()
                .requestMatchers(GET, "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(POST, "/api/auth/login").permitAll()
                .requestMatchers(POST, "/api/auth/refresh").permitAll()
                .requestMatchers(POST, "/api/auth/logout").permitAll()
                .requestMatchers(POST, "/api/auth/register").hasRole("ADMIN")
                .requestMatchers(GET, "/api/users/veterinarians").hasAnyRole("ADMIN", "RECEPCIONISTA")
                .requestMatchers(GET, "/api/users").hasRole("ADMIN")
                .requestMatchers(GET, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(PUT, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(PATCH, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(POST, "/api/consultations").hasRole("VETERINARIO")
                .requestMatchers(GET, "/api/consultations/**").authenticated()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

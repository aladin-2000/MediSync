package com.project.medisync.modules.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration centrale de la sécurité : définit quelles routes sont publiques,
 * lesquelles nécessitent d'être connecté, et lesquelles nécessitent un rôle précis.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Pré-vol CORS du navigateur — toujours autorisé
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Authentification — public
                        .requestMatchers("/auth/login").permitAll()

                        // Consultation des médecins et créneaux — public (recherche de médecin sans compte)
                        .requestMatchers(HttpMethod.GET, "/medecins/**").permitAll()

                        // Profil du medecin/delegue connecte lui-meme — avant les regles ADMIN plus larges ci-dessous
                        .requestMatchers(HttpMethod.PUT, "/medecins/mon-profil").hasRole("MEDECIN")
                        .requestMatchers(HttpMethod.PUT, "/api/delegues/mon-profil").hasRole("DELEGUE")

                        // Gestion des médecins et des comptes — réservé aux admins
                        .requestMatchers(HttpMethod.POST, "/medecins/creer-medecin-complet").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/medecins").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/medecins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/medecins/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Gestion des créneaux — admin ou le médecin lui-même
                        .requestMatchers(HttpMethod.POST, "/medecins/creneaux/**").hasAnyRole("ADMIN", "MEDECIN")
                        .requestMatchers(HttpMethod.DELETE, "/medecins/creneaux/**").hasAnyRole("ADMIN", "MEDECIN")

                        // Rendez-vous — réservé au délégué, sauf consultation et confirmation/annulation côté médecin
                        .requestMatchers(HttpMethod.PATCH, "/api/rendezvous/*/annuler-medecin").hasRole("MEDECIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/rendezvous/*/realise-medecin").hasRole("MEDECIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/rendezvous/*/realise-delegue").hasRole("DELEGUE")
                        .requestMatchers(HttpMethod.PATCH, "/api/rendezvous/*/absent-delegue").hasRole("MEDECIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/rendezvous/*/absent-medecin").hasRole("DELEGUE")
                        .requestMatchers(HttpMethod.GET, "/api/rendezvous/medecin/**").hasAnyRole("DELEGUE", "MEDECIN")
                        .requestMatchers("/api/rendezvous/**").hasRole("DELEGUE")

                        // Tout le reste : il faut juste être connecté
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

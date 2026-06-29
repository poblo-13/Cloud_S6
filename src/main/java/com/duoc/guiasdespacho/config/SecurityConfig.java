package com.duoc.guiasdespacho.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz

                // ROLE_consulta y ROLE_admin pueden descargar guías
                .requestMatchers("/api/guias/descargar/**")
                    .hasAnyAuthority("ROLE_consulta", "ROLE_admin")

                // Solo ROLE_admin puede usar el resto de endpoints de guías:
                // crear, actualizar, eliminar, buscar y subir a S3
                .requestMatchers("/api/guias/**")
                    .hasAuthority("ROLE_admin")

                // Cualquier otra ruta requiere autenticación
                .anyRequest()
                    .authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Claim personalizado que viene desde Azure AD B2C
        grantedAuthoritiesConverter.setAuthoritiesClaimName("extension_consultaRole");

        // Si Azure entrega "admin", Spring lo convierte en "ROLE_admin"
        // Si Azure entrega "consulta", Spring lo convierte en "ROLE_consulta"
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
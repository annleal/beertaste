package com.beertaste.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // deshabilita CSRF para pruebas con Postman
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated() // /api requiere autenticación
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults()); // habilita Basic Auth

        return http.build();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Almacena la contraseña tal cual, no encriptada
        return NoOpPasswordEncoder.getInstance();
    }
}

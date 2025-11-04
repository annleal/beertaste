package com.beertaste.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * PasswordEncoder para desarrollo.
     * En producción se recomienda BCryptPasswordEncoder u otro encoder seguro
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    return new Base64PasswordEncoder();
    }

    /**
     * Configuración de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF solo para pruebas
            .csrf(csrf -> csrf.disable())
            // Configuración de autorización
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login","/register", "/forgot-password", "/css/**", "/img/**", "/js/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            // Login con formulario personalizado
            .formLogin(form -> form
                .loginPage("/login")                 // Página de login
                .loginProcessingUrl("/login")        // URL de envío del formulario
                .defaultSuccessUrl("/home", true)    // Redirige a home tras login correcto
                .failureUrl("/login?error=true")     // Si falla, vuelve con ?error
                .permitAll()
            )
            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            // Habilita Basic Auth (opcional)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

package com.example.booksocial_frontend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion basica de seguridad del frontend para permitir las rutas y recursos necesarios.
 * Se usa para organizar mejor la logica del frontend Thymeleaf.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll())
        .formLogin(login -> login.disable())
        .httpBasic(basic -> basic.disable());

    return http.build();
  }
}
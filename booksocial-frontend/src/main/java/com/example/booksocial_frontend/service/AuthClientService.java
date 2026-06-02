package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.JwtResponseDTO;
import com.example.booksocial_frontend.dto.LoginRequestDTO;
import com.example.booksocial_frontend.dto.RegisterRequestDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de auth del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /auth.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class AuthClientService {

    /** @Value lee la URL base del backend desde application.properties o usa un valor por defecto. */
    @Value("${api.base-url:http://localhost:9999/api}")
    private String apiBaseUrl;

    /** @Autowired inyecta automaticamente el interceptor o dependencia que necesita el servicio. */
    @Autowired
    private SessionJwtInterceptor jwtInterceptor;

    private RestClient restClient;

    /** @PostConstruct ejecuta init despues de crear el servicio para preparar el RestClient. */
    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
            .baseUrl(apiBaseUrl + "/auth")
            .requestInterceptor(jwtInterceptor)
            .build();
    }

    /**
     * Envia las credenciales al backend y recibe el token de sesion.
     * Usa RestClient o datos de sesion para devolver la informacion al controlador.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    public JwtResponseDTO login(LoginRequestDTO request) {
        return restClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JwtResponseDTO.class);
    }

    /**
     * Envia al backend los datos para registrar un usuario nuevo.
     * Usa RestClient o datos de sesion para devolver la informacion al controlador.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    public void register(RegisterRequestDTO request) {
        restClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}

package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.SubscriptionRequestDTO;
import com.example.booksocial_frontend.dto.SubscriptionResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de subscription del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /subscriptions.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class SubscriptionClientService {

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
            .baseUrl(apiBaseUrl + "/subscriptions")
            .requestInterceptor(jwtInterceptor)
            .build();
    }

    /**
     * Pide al backend by user id.
     * Usa RestClient o datos de sesion para devolver la informacion al controlador.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    public SubscriptionResponseDTO getByUserId(Long userId) {
        return restClient.get()
                .uri("/user/{userId}", userId)
                .retrieve()
                .body(SubscriptionResponseDTO.class);
    }

    /**
     * Cancela la suscripcion del usuario en el backend.
     * Usa RestClient o datos de sesion para devolver la informacion al controlador.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    public void cancel(Long userId) {
        restClient.delete()
                .uri("/user/{userId}", userId)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Activa la suscripcion del usuario en el backend.
     * Usa RestClient o datos de sesion para devolver la informacion al controlador.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    public SubscriptionResponseDTO activate(Long userId) {
        SubscriptionRequestDTO req = new SubscriptionRequestDTO();
        req.setUserId(userId);
        return restClient.post()
                .body(req)
                .retrieve()
                .body(SubscriptionResponseDTO.class);
    }
}

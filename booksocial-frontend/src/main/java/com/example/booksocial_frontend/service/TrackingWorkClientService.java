package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.TrackingWorkRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de tracking work del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /tracking-works.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class TrackingWorkClientService {

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
        .baseUrl(apiBaseUrl + "/tracking-works")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend by user.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<TrackingWorkResponseDTO> getByUser(Long userId) {
    return restClient.get()
        .uri("/user/{userId}", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<TrackingWorkResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear .
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public TrackingWorkResponseDTO create(TrackingWorkRequestDTO request) {
    return restClient.post()
        .uri("")
        .body(request)
        .retrieve()
        .body(TrackingWorkResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar .
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public TrackingWorkResponseDTO update(Long trackingId, TrackingWorkRequestDTO request) {
    return restClient.put()
        .uri("/{id}", trackingId)
        .body(request)
        .retrieve()
        .body(TrackingWorkResponseDTO.class);
  }

  /**
   * Pide al backend eliminar .
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void delete(Long trackingId) {
    restClient.delete()
        .uri("/{id}", trackingId)
        .retrieve()
        .toBodilessEntity();
  }
}

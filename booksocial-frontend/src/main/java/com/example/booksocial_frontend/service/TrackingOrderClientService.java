package com.example.booksocial_frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.booksocial_frontend.dto.TrackingOrderRequestDTO;
import com.example.booksocial_frontend.dto.TrackingOrderResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de tracking order del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /tracking-orders.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class TrackingOrderClientService {

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
        .baseUrl(apiBaseUrl + "/tracking-orders")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend latest by order.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public TrackingOrderResponseDTO getLatestByOrder(Long orderId) {
    try {
      return restClient.get()
          .uri("/order/{orderId}/latest", orderId)
          .retrieve()
          .body(TrackingOrderResponseDTO.class);
    } catch (RestClientResponseException e) {
      return null;
    }
  }

  /**
   * Envia al backend los datos para crear tracking.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public TrackingOrderResponseDTO createTracking(Long orderId, String status) {
    TrackingOrderRequestDTO dto = new TrackingOrderRequestDTO(orderId, status);
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .body(TrackingOrderResponseDTO.class);
  }
}

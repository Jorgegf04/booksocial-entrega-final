package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.EventRequestDTO;
import com.example.booksocial_frontend.dto.EventResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de event del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /events.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class EventClientService {

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
        .baseUrl(apiBaseUrl + "/events")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de events.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<EventResponseDTO> getAllEvents() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EventResponseDTO>>() {});
  }

  /**
   * Pide al backend upcoming events.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<EventResponseDTO> getUpcomingEvents() {
    return restClient.get()
        .uri("/upcoming")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EventResponseDTO>>() {});
  }

  /**
   * Pide al backend event by id.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EventResponseDTO getEventById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  /**
   * Envia al backend la peticion para apuntarse al evento.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EventResponseDTO joinEvent(Long eventId, Long userId) {
    return restClient.post()
        .uri("/{id}/join?userId={userId}", eventId, userId)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  /**
   * Envia al backend la peticion para salir del evento.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EventResponseDTO leaveEvent(Long eventId, Long userId) {
    return restClient.delete()
        .uri("/{id}/leave?userId={userId}", eventId, userId)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  /**
   * Envia al backend los datos para crear event.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EventResponseDTO createEvent(EventRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar event.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EventResponseDTO updateEvent(Long id, EventRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EventResponseDTO.class);
  }

  /**
   * Pide al backend eliminar event.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteEvent(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

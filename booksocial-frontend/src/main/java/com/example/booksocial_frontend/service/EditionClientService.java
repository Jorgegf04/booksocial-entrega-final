package com.example.booksocial_frontend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.EditionRequestDTO;
import com.example.booksocial_frontend.dto.EditionResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de edition del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /editions.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class EditionClientService {

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
        .baseUrl(apiBaseUrl + "/editions")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de editions.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<EditionResponseDTO> getAllEditions() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<EditionResponseDTO>>() {});
  }

  /**
   * Pide al backend editions by work.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<EditionResponseDTO> getEditionsByWork(Long workId) {
    return getAllEditions().stream()
        .filter(e -> workId.equals(e.getWorkId()))
        .collect(Collectors.toList());
  }

  /**
   * Envia al backend los datos para crear edition.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EditionResponseDTO createEdition(EditionRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EditionResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar edition.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public EditionResponseDTO updateEdition(Long id, EditionRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(EditionResponseDTO.class);
  }

  /**
   * Pide al backend eliminar edition.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteEdition(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

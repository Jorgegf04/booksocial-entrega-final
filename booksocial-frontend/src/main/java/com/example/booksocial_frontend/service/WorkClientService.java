package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.WorkRequestDTO;
import com.example.booksocial_frontend.dto.WorkResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

// Codigo de la ilustracion 43
/**
 * Servicio de work del frontend Thymeleaf.
 * 
 * @Service hace que Spring cree esta clase como componente para poder
 *          inyectarla.
 *          Usa del backend: /works.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class WorkClientService {

  /**
   * @Value lee la URL base del backend desde application.properties o usa un
   *        valor por defecto.
   */
  @Value("${api.base-url:http://localhost:9999/api}")
  private String apiBaseUrl;

  /**
   * @Autowired inyecta automaticamente el interceptor o dependencia que necesita
   *            el servicio.
   */
  @Autowired
  private SessionJwtInterceptor jwtInterceptor;

  private RestClient restClient;

  /**
   * @PostConstruct ejecuta init despues de crear el servicio para preparar el
   *                RestClient.
   */
  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder()
        .baseUrl(apiBaseUrl + "/works")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de works.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<WorkResponseDTO> getAllWorks() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<WorkResponseDTO>>() {
        });
  }

  /**
   * Pide al backend work by id.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public WorkResponseDTO getWorkById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  /**
   * Busca datos en el backend usando los filtros recibidos.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<WorkResponseDTO> search(String title, String genre, Double rating) {
    return restClient.get()
        .uri(uriBuilder -> {
          var builder = uriBuilder.path("/search");
          if (title != null && !title.isBlank())
            builder = builder.queryParam("title", title);
          if (genre != null)
            builder = builder.queryParam("genre", genre);
          if (rating != null && rating > 0)
            builder = builder.queryParam("rating", rating);
          return builder.build();
        })
        .retrieve()
        .body(new ParameterizedTypeReference<List<WorkResponseDTO>>() {
        });
  }

  /**
   * Envia al backend los datos para crear work.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public WorkResponseDTO createWork(WorkRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar work.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public WorkResponseDTO updateWork(Long id, WorkRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(WorkResponseDTO.class);
  }

  /**
   * Pide al backend eliminar work.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteWork(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

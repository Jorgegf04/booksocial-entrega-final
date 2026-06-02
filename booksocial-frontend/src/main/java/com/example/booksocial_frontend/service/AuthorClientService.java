package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.AuthorRequestDTO;
import com.example.booksocial_frontend.dto.AuthorResponseDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de author del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /authors.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class AuthorClientService {

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
        .baseUrl(apiBaseUrl + "/authors")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de authors.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<AuthorResponseDTO> getAllAuthors() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<AuthorResponseDTO>>() {});
  }

  /**
   * Pide al backend author by id.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public AuthorResponseDTO getAuthorById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(AuthorResponseDTO.class);
  }

  /**
   * Envia al backend los datos para crear author.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public AuthorResponseDTO createAuthor(AuthorRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(AuthorResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar author.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(AuthorResponseDTO.class);
  }

  /**
   * Pide al backend eliminar author.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteAuthor(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * Envia al backend la peticion para seguir el elemento indicado.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void followAuthor(Long authorId, Long userId) {
    restClient.post()
        .uri("/{id}/follow?userId={userId}", authorId, userId)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * Envia al backend la peticion para dejar de seguir el elemento indicado.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void unfollowAuthor(Long authorId, Long userId) {
    restClient.delete()
        .uri("/{id}/follow?userId={userId}", authorId, userId)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * Pide al backend followers.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<UserResponseDTO> getFollowers(Long authorId) {
    try {
      return restClient.get()
          .uri("/{id}/followers", authorId)
          .retrieve()
          .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
    } catch (Exception e) {
      return List.of();
    }
  }

  /**
   * Ejecuta la operacion is following del servicio.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public boolean isFollowing(Long authorId, Long userId) {
    try {
      Boolean result = restClient.get()
          .uri("/{id}/following?userId={userId}", authorId, userId)
          .retrieve()
          .body(Boolean.class);
      return Boolean.TRUE.equals(result);
    } catch (Exception e) {
      return false;
    }
  }
}

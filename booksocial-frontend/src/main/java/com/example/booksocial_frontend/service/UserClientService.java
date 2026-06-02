package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.CreateUserRequestDTO;
import com.example.booksocial_frontend.dto.TrackingWorkResponseDTO;
import com.example.booksocial_frontend.dto.UpdateUserRequestDTO;
import com.example.booksocial_frontend.dto.UserResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de user del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /users.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class UserClientService {

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
        .baseUrl(apiBaseUrl + "/users")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de users.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<UserResponseDTO> getAllUsers() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
  }

  /**
   * Pide al backend user by id.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public UserResponseDTO getUserById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(UserResponseDTO.class);
  }

  /**
   * Pide al backend tracking by user.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<TrackingWorkResponseDTO> getTrackingByUser(Long userId) {
    return restClient.get()
        .uri("/{id}/tracking", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<TrackingWorkResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para actualizar user.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(UserResponseDTO.class);
  }

  /**
   * Pide al backend eliminar user.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteUser(Long id) {
    restClient.delete()
        .uri("/{id}", id)
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
  public List<UserResponseDTO> getFollowers(Long userId) {
    return restClient.get()
        .uri("/{id}/followers", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
  }

  /**
   * Pide al backend following.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<UserResponseDTO> getFollowing(Long userId) {
    return restClient.get()
        .uri("/{id}/following", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<UserResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear user.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public UserResponseDTO createUser(CreateUserRequestDTO dto) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .body(UserResponseDTO.class);
  }

  /**
   * Envia al backend la peticion para seguir el elemento indicado.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void followUser(Long followerId, Long targetId) {
    restClient.post()
        .uri("/{id}/follow/{targetId}", followerId, targetId)
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
  public void unfollowUser(Long followerId, Long targetId) {
    restClient.delete()
        .uri("/{id}/follow/{targetId}", followerId, targetId)
        .retrieve()
        .toBodilessEntity();
  }
}

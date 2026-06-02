package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.ReactionRequestDTO;
import com.example.booksocial_frontend.dto.ReactionResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de reaction del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /reactions.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class ReactionClientService {

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
        .baseUrl(apiBaseUrl + "/reactions")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Cambia en el backend el estado indicado segun el usuario actual.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public ReactionResponseDTO toggleReaction(Long userId, Long commentId) {
    ReactionRequestDTO req = new ReactionRequestDTO(userId, commentId);
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(req)
        .retrieve()
        .body(ReactionResponseDTO.class);
  }

  /**
   * Pide al backend reactions by comment.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<ReactionResponseDTO> getReactionsByComment(Long commentId) {
    return restClient.get()
        .uri("/comment/{commentId}", commentId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<ReactionResponseDTO>>() {});
  }

  /**
   * Pide al backend reaction count.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public int getReactionCount(Long commentId) {
    Integer count = restClient.get()
        .uri("/comment/{commentId}/count", commentId)
        .retrieve()
        .body(Integer.class);
    return count != null ? count : 0;
  }
}

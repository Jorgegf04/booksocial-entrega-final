package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.CommentRequestDTO;
import com.example.booksocial_frontend.dto.CommentResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de comment del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /comments.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class CommentClientService {

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
        .baseUrl(apiBaseUrl + "/comments")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend root comments by work.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<CommentResponseDTO> getRootCommentsByWork(Long workId) {
    return restClient.get()
        .uri("/work/{workId}/root", workId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponseDTO>>() {});
  }

  /**
   * Pide al backend el listado completo de comments.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<CommentResponseDTO> getAllComments() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear comment.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public CommentResponseDTO createComment(CommentRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(CommentResponseDTO.class);
  }

  /**
   * Ejecuta la operacion reply to comment del servicio.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public CommentResponseDTO replyToComment(Long parentId, CommentRequestDTO request) {
    return restClient.post()
        .uri("/{parentId}/reply", parentId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(CommentResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar comment.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void updateComment(Long id, String content) {
    CommentRequestDTO req = new CommentRequestDTO();
    req.setContent(content);
    restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(req)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * Pide al backend eliminar comment.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteComment(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

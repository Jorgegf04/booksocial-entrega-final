package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.TomeRequestDTO;
import com.example.booksocial_frontend.dto.TomeResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de tome del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /tomes.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class TomeClientService {

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
        .baseUrl(apiBaseUrl + "/tomes")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de tomes.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<TomeResponseDTO> getAllTomes() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<TomeResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear tome.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public TomeResponseDTO createTome(TomeRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(TomeResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar tome.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public TomeResponseDTO updateTome(Long id, TomeRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(TomeResponseDTO.class);
  }

  /**
   * Pide al backend eliminar tome.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteTome(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

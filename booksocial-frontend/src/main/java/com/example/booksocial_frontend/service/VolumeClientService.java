package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.VolumeRequestDTO;
import com.example.booksocial_frontend.dto.VolumeResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de volume del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /volumes.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class VolumeClientService {

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
        .baseUrl(apiBaseUrl + "/volumes")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de volumes.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<VolumeResponseDTO> getAllVolumes() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<VolumeResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear volume.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public VolumeResponseDTO createVolume(VolumeRequestDTO request) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(VolumeResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar volume.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public VolumeResponseDTO updateVolume(Long id, VolumeRequestDTO request) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(VolumeResponseDTO.class);
  }

  /**
   * Pide al backend eliminar volume.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteVolume(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

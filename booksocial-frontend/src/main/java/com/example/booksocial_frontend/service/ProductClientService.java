package com.example.booksocial_frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.ProductRequestDTO;
import com.example.booksocial_frontend.dto.ProductResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de product del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /products.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class ProductClientService {

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
        .baseUrl(apiBaseUrl + "/products")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend available products.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<ProductResponseDTO> getAvailableProducts() {
    return restClient.get()
        .uri("/available")
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductResponseDTO>>() {});
  }

  /**
   * Pide al backend products by work.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<ProductResponseDTO> getProductsByWork(Long workId) {
    return restClient.get()
        .uri("/work/{workId}", workId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductResponseDTO>>() {});
  }

  /**
   * Pide al backend product by id.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public ProductResponseDTO getProductById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(ProductResponseDTO.class);
  }

  /**
   * Pide al backend products by edition.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<ProductResponseDTO> getProductsByEdition(Long editionId) {
    return restClient.get()
        .uri("/edition/{editionId}", editionId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear product.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public ProductResponseDTO createProduct(Long editionId, Double price, Integer stock) {
    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ProductRequestDTO(price, stock, editionId))
        .retrieve()
        .body(ProductResponseDTO.class);
  }

  /**
   * Envia al backend los datos para actualizar product.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public ProductResponseDTO updateProduct(Long id, Double price, Integer stock, Long editionId) {
    return restClient.put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ProductRequestDTO(price, stock, editionId))
        .retrieve()
        .body(ProductResponseDTO.class);
  }
}

package com.example.booksocial_frontend.service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.booksocial_frontend.dto.OrderResponseDTO;
import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de order del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: /orders.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class OrderClientService {

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
        .baseUrl(apiBaseUrl + "/orders")
        .requestInterceptor(jwtInterceptor)
        .build();
  }

  /**
   * Pide al backend el listado completo de orders.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<OrderResponseDTO> getAllOrders() {
    return restClient.get()
        .uri("")
        .retrieve()
        .body(new ParameterizedTypeReference<List<OrderResponseDTO>>() {});
  }

  /**
   * Pide al backend order by id.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public OrderResponseDTO getOrderById(Long id) {
    return restClient.get()
        .uri("/{id}", id)
        .retrieve()
        .body(OrderResponseDTO.class);
  }

  /**
   * Pide al backend orders by user.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public List<OrderResponseDTO> getOrdersByUser(Long userId) {
    return restClient.get()
        .uri("/user/{userId}", userId)
        .retrieve()
        .body(new ParameterizedTypeReference<List<OrderResponseDTO>>() {});
  }

  /**
   * Envia al backend los datos para crear order.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public OrderResponseDTO createOrder(Long userId, List<Map<String, Object>> lines) {
    return createOrder(userId, null, lines);
  }

  /**
   * Envia al backend los datos para crear order.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public OrderResponseDTO createOrder(Long userId, String guestEmail, List<Map<String, Object>> lines) {
    Map<String, Object> body = new HashMap<>();
    if (userId != null) {
      body.put("userId", userId);
    }
    if (guestEmail != null && !guestEmail.isBlank()) {
      body.put("guestEmail", guestEmail.trim());
    }
    body.put("orderLines", lines);

    return restClient.post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .body(OrderResponseDTO.class);
  }

  /**
   * Pide al backend eliminar order.
   * Usa RestClient o datos de sesion para devolver la informacion al controlador.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  public void deleteOrder(Long id) {
    restClient.delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();
  }
}

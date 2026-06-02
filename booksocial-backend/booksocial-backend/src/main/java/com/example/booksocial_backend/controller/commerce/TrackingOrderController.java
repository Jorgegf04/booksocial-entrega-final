package com.example.booksocial_backend.controller.commerce;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.commerce.TrackingOrderRequestDTO;
import com.example.booksocial_backend.DTO.commerce.TrackingOrderResponseDTO;
import com.example.booksocial_backend.service.TrackingOrderService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona el seguimiento de estados de los pedidos.
 * Permite registrar cambios de estado y consultar el ultimo estado disponible de un pedido.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "TrackingOrderController", description = "API REST para el seguimiento de pedidos")
@RestController
@RequestMapping("/api/tracking-orders")
@RequiredArgsConstructor
public class TrackingOrderController {

  private final TrackingOrderService trackingService;

  /**
   * Crea un recurso en el sistema y devuelve la respuesta con los datos actualizados.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Crear recurso", description = "Registra un nuevo recurso y devuelve los datos generados por el servicio.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<TrackingOrderResponseDTO> create(
      @Valid @RequestBody TrackingOrderRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(trackingService.addTracking(request));
  }

  /**
   * Endpoint que sirve para crear varios recursos de golpe, util para cargas iniciales o pruebas desde Postman.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Creacion masiva", description = "Registra varios recursos en una sola peticion y devuelve el listado creado.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Recurso creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/batch")
  public ResponseEntity<List<TrackingOrderResponseDTO>> createMany(
      @Valid @RequestBody List<TrackingOrderRequestDTO> requests) {
    if (requests == null || requests.isEmpty()) {
      throw new IllegalArgumentException("La lista de tracking no puede estar vacia");
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(trackingService::addTracking).toList());
  }

  /**
   * Endpoint que devuelve el ultimo estado registrado de un pedido.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Ultimo tracking de pedido", description = "Recupera el estado mas reciente asociado al pedido indicado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/order/{orderId}/latest")
  public ResponseEntity<TrackingOrderResponseDTO> getLatest(@PathVariable Long orderId) {
    return ResponseEntity.ok(trackingService.getLatestTracking(orderId));
  }
}

package com.example.booksocial_backend.controller.commerce;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.booksocial_backend.DTO.commerce.OrderRequestDTO;
import com.example.booksocial_backend.DTO.commerce.OrderResponseDTO;
import com.example.booksocial_backend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona los pedidos realizados en el sistema de compra de BookSocial.
 * Permite crear pedidos, consultarlos por usuario o identificador y eliminarlos cuando corresponde.
 * Centraliza la información principal del proceso de compra.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "OrderController", description = "API REST para la gestión de pedidos de compra")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  // CREATE — público para permitir checkout como invitado (sin cuenta)

  /**
   * Registra pedido en el sistema y devuelve el recurso creado con sus datos actualizados.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Crear pedido", description = "Registra un nuevo pedido. Accesible sin cuenta (checkout de invitado).")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Pedido creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos invalidos o stock insuficiente"),
      @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del pedido"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PostMapping
  public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
  }

  /**
   * Registra varios recursos en una sola petición y devuelve el listado creado por el servicio.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Creación masiva de pedidos", description = "Registra varios recursos en una sola petición y devuelve el listado creado por el servicio.")
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
  public ResponseEntity<List<OrderResponseDTO>> createMany(@Valid @RequestBody List<OrderRequestDTO> requests) {
    if (requests == null || requests.isEmpty())
      throw new IllegalArgumentException("La lista de pedidos no puede estar vacía");
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(requests.stream().map(orderService::createOrder).toList());
  }

  // READ — listado global solo ADMIN; por ID o usuario requiere autenticación

  /**
   * Devuelve el listado de recursos disponibles para su consulta desde el cliente.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar pedidos", description = "Devuelve el listado de recursos disponibles para su consulta desde el cliente.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<OrderResponseDTO>> getAll() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }

  /**
   * Recupera un recurso concreto mediante su identificador y devuelve sus datos completos.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Obtener pedido por ID", description = "Recupera un recurso concreto mediante su identificador y devuelve sus datos completos.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  /**
   * Ejecuta la operación 'Pedidos por usuario' y devuelve la respuesta generada por el servicio correspondiente.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Pedidos por usuario", description = "Ejecuta la operación 'Pedidos por usuario' y devuelve la respuesta generada por el servicio correspondiente.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<OrderResponseDTO>> getByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(orderService.getOrdersByUser(userId));
  }

  // DELETE — solo ADMIN

  /**
   * Elimina o cancela el recurso indicado y devuelve una respuesta sin contenido cuando finaliza correctamente.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Eliminar pedido", description = "Elimina o cancela el recurso indicado y devuelve una respuesta sin contenido cuando finaliza correctamente.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}

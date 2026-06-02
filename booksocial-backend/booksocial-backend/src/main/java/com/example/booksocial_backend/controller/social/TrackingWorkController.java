package com.example.booksocial_backend.controller.social;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booksocial_backend.DTO.social.TrackingWorkRequestDTO;
import com.example.booksocial_backend.DTO.social.TrackingWorkResponseDTO;
import com.example.booksocial_backend.service.TrackingWorkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona el seguimiento de obras por parte de los usuarios.
 * Permite registrar obras en biblioteca o wishlist, consultar por usuario, actualizar estado y eliminar seguimientos.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "TrackingWorkController", description = "API REST para el seguimiento de obras por usuario")
@RestController
@RequestMapping("/api/tracking-works")
@RequiredArgsConstructor
public class TrackingWorkController {

  private final TrackingWorkService service;

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
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<TrackingWorkResponseDTO> create(@RequestBody TrackingWorkRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
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
  public ResponseEntity<List<TrackingWorkResponseDTO>> createMany(
      @RequestBody List<TrackingWorkRequestDTO> requests) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createMany(requests));
  }

  /**
   * Endpoint que devuelve los recursos asociados a un usuario.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Listar por usuario", description = "Recupera los recursos vinculados al usuario indicado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<TrackingWorkResponseDTO>> getByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(service.getByUser(userId));
  }

  /**
   * Endpoint que sirve para actualizar el estado de seguimiento de una obra.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Actualizar estado de seguimiento", description = "Modifica el estado de lectura o seguimiento de una obra para el usuario.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Recurso actualizado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  public ResponseEntity<TrackingWorkResponseDTO> updateStatus(@PathVariable Long id,
      @RequestBody TrackingWorkRequestDTO request) {
    return ResponseEntity.ok(service.updateStatus(id, request));
  }

  /**
   * Endpoint que sirve para eliminar un recurso del sistema.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Eliminar recurso", description = "Elimina el recurso indicado y devuelve una respuesta sin contenido.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Recurso eliminado correctamente"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
      @ApiResponse(responseCode = "409", description = "No se puede eliminar por restricciones del recurso"),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  })
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
